#include "nlab.h"

int main(int argc, char* argv[])
{
   test();

   if(argc != 2){
      fprintf(stderr, "Usage : %s <test.nlb>\n", argv[0]);
      exit(EXIT_FAILURE);
   }
   char * FNAME=argv[1];
   FILE* fp = nfopen(FNAME, "r");
   
   Program* p =ncalloc(1, sizeof(Program));
   int i=0;
   while(fscanf(fp,"%s", p->wds[i++])==1 && i<MAXNUMTOKENS);
   assert(i<MAXNUMTOKENS);
   prog(p);
   fclose(fp);
   free(p);
   #ifdef INTERP
   return 0;
   #else
   printf("Parsed OK\n");
   #endif
   
   return 0;
}

void test(void)
{
   //valid varnames
   assert(varname("$H"));
   assert(varname("$G"));

   //invalid varnames
   assert(!varname("$a"));
   assert(!varname("$-"));
   assert(!varname("A"));
   assert(!varname(""));
   assert(!varname(NULL));
   assert(!varname("-"));

   //valid strings
   assert(string("\"abc\""));
   assert(string("\"../test/testcases/t_instrclist0.nlb\""));
   assert(string("\"\""));
   assert(string("\"\""));
   assert(string("\"=\""));
   
   //invalid strings
   assert(!string("\"abc"));
   assert(!string("abc\""));
   assert(!string("abc"));
   assert(!string("a\"b"));
   assert(!string("--"));
   assert(!string(""));
   assert(!string(NULL));

   //valid integers
   assert(integer("0"));
   assert(integer("95"));
   assert(integer("123456"));

   //invalid integers
   assert(!integer("abc"));
   assert(!integer(NULL));
   assert(!integer("17.5"));
   assert(!integer(" "));
   assert(!integer("-"));
   assert(!integer("12a"));

   //valid pushdowns
   assert(varname("$H"));
   assert(varname("$G"));
   assert(integer("0"));
   assert(integer("95"));
   assert(integer("123456"));

   //invalid pushdowns
   assert(!varname("$a"));
   assert(!varname("$-"));
   assert(!varname("A"));
   assert(!varname(""));
   assert(!varname(" "));
   assert(!varname("-"));
   assert(!integer("abc"));
   assert(!integer("1-"));
   assert(!integer("17.5"));
   assert(!integer(NULL));
   assert(!integer("-"));
   assert(!integer("12a"));

   //valid unaryops
   assert(unaryop("U-NOT"));
   assert(unaryop("U-EIGHTCOUNT"));

   //invalid unaryops
   assert(!unaryop("UNOT"));
   assert(!unaryop(NULL));

   //valid binaryops
   assert(binaryop("B-AND"));
   assert(binaryop("B-OR"));
   assert(binaryop("B-GREATER"));
   assert(binaryop("B-LESS"));
   assert(binaryop("B-ADD"));
   assert(binaryop("B-TIMES"));
   assert(binaryop("B-EQUALS"));

   //invalid binaryop
   assert(!binaryop("BADD"));
   assert(!binaryop(NULL));
   assert(!binaryop("-"));

   #ifdef EXTENSION
   //valid boolops
   assert(boolop("B-AND"));
   assert(boolop("B-OR"));
   assert(boolop("B-GREATER"));
   assert(boolop("B-LESS"));
   assert(boolop("B-EQUALS"));

   //invalid boolop
   assert(!boolop("B-ADD"));
   assert(!boolop("B-TIMES"));
   #endif

   Program* p =ncalloc(1, sizeof(Program));
   assert(p->v[21].col==0);
   free(p);
   
   #ifdef INTERP

   //bool u_not(var* a) function(white-box testing)
   // check if var a is bigger than size 0
   //flip the boolean values
   var* v=ncalloc(1,sizeof(var));
   assert(!u_not(v));

   v->row=3;
   v->col=3;
   v->matrix[0][0]=5;
   v->matrix[1][2]=1;
   assert(u_not(v));
   assert(!v->matrix[0][0]);
   assert(!v->matrix[1][2]);
   assert(v->matrix[2][2]);
   free(v);
 
   var* a=ncalloc(1,sizeof(var));
   var* b=ncalloc(1,sizeof(var));
   var* c=ncalloc(1,sizeof(var));
   a->row=3;
   a->col=3;
   a->matrix[0][2]=1;
   a->matrix[1][0]=1;
   a->matrix[1][1]=1;
   a->matrix[2][1]=1;
   a->matrix[0][0]=5;

   b->matrix[0][1]=1;
   b->matrix[1][2]=10;

   //white-box testing 
   assert(u_eightcount(a,c));
   assert(c->matrix[1][1]==4);
   assert(c->matrix[1][0]==3);  

   //black-box testing on _bop()
   _bop(c,b,a,"B-AND");

   assert(a->matrix[0][0]==0);
   assert(a->matrix[1][2]==1);

   _bop(c,b,a,"B-OR");
   assert(a->matrix[2][2]==1);
   assert(a->matrix[2][1]==1);
   _bop(c,b,a,"B-GREATER");
   assert(a->matrix[0][1]==1);
   assert(a->matrix[1][2]==0);
   _bop(c,b,a,"B-LESS");
   assert(a->matrix[0][1]==0);
   assert(a->matrix[1][2]==1);
   _bop(c,b,a,"B-ADD");
   assert(a->matrix[0][1]==5);
   assert(a->matrix[1][2]==13);
   _bop(c,b,a,"B-TIMES");
   assert(a->matrix[0][1]==4);
   assert(a->matrix[1][2]==30);
   assert(a->matrix[1][1]==0);
   b->matrix[0][2]=1;
   _bop(c,b,a,"B-EQUALS");
   assert(a->matrix[0][2]==1);
   assert(a->matrix[1][2]==0);


   free(a);
   free(b);
   free(c);

   //white-box testing
   //initialize a var with integer x
   var n=inttovar(3);
   assert(n.matrix[0][0]==3);
   assert(n.matrix[10][17]==0);

   //black-box testing
   //extract filename from string with double quotes
   void extractstr(char* dest, char*src);
   char x[MAXTOKENSIZE]="",y[MAXTOKENSIZE]="\"filename\"";
   extractstr(x,y);
   assert(x[0]=='f');
   assert(x[7]=='e');

   //White-box testing with assert()
   //Checks if var size equals 1x1
   var* e=ncalloc(1,sizeof(var));
   var* f=ncalloc(1,sizeof(var));
   
   e->row=1;
   e->col=1;
   e->matrix[0][0]=7;
   f->row=2;
   f->col=3;
   f->matrix[1][2]=5;

   assert(checkint(e));
   assert(e->isint);
   assert(!checkint(f));
   assert(!f->isint);

   //Checks the size of two vars  
   assert(match(e,f));
   assert(e->col==3);
   assert(e->matrix[1][2]==7);
   e->row=3;
   assert(!match(e,f));

   free(e);
   free(f);

   #endif
}

void prog(Program* p){

   if(!p){
      ERROR("NULL Program");
   }

   if(!strsame(p->wds[p->cw], "BEGIN")){ 
      ERROR("No BEGIN statement ?");
   }
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw], "{")){
      ERROR("MISSING { ?");
   }
   p->cw = p->cw + 1;
   
   instrclist(p);
   return;

}


void instrclist(Program*p)
{  
   if(!p){
      ERROR("NULL Program");
   }
   if(strsame(p->wds[p->cw], "}")){
      return;
   }
   instrc(p);
   p->cw = p->cw + 1;
   instrclist(p);
   return;
}

void instrc(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(strsame(p->wds[p->cw],"PRINT")){
      p->cw = p->cw + 1;
      print(p);       
   }
   else if(strsame(p->wds[p->cw],"SET")){
      p->cw = p->cw + 1;
      set(p);
   }
   else if(strsame(p->wds[p->cw],"ONES")){
      p->cw = p->cw + 1;
      ones(p);
   }
   else if(strsame(p->wds[p->cw],"READ") ){
      p->cw = p->cw + 1;
      read(p);
   }
   else if(strsame(p->wds[p->cw],"LOOP")){
      p->cw = p->cw + 1;
      loop(p);
   }
   #ifdef EXTENSION
   else if(strsame(p->wds[p->cw],"IF")){
      p->cw = p->cw + 1;
      ifstmt(p);
   }
   #endif
   else{
      ERROR("Invalid Instruction");
   } 
   return;  
}

void print(Program*p)
{  
   if(!p){
      ERROR("NULL Program");
   }
   if(varname(p->wds[p->cw])){
      #ifdef INTERP
      int var =(int)(p->wds[p->cw][1]-'A');
      if(p->v[var].row==0||p->v[var].col==0){
	   printf("variable not defined\n");
      }
      printvar(p->v+var);
      #endif 
      return;
   }
   else if(string(p->wds[p->cw])){
      #ifdef INTERP
      int l=strlen(p->wds[p->cw]);
      for(int i=1;i<l-1;i++){
	      printf("%c",p->wds[p->cw][i]);
      }
	   printf("\n");
      #endif
      return;
   }
   else{
      ERROR("Invalid PRINT Statement"); 
   } 
}

void set(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(!varname(p->wds[p->cw])){
      ERROR("Invalid SET Statement");
   }
   #ifdef INTERP
   int lefthand =(int)(p->wds[p->cw][1]-'A');
   #ifdef EXTENSION
   if(p->v[lefthand].isindex){
      ERROR("Cannot access loop index");
   }
   #endif
   #endif   
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw],":=")){
      ERROR("Invalid SET Statement");
   }
   p->cw = p->cw + 1;
   #ifdef INTERP
   p->s=stack_init();
   #endif
   polishlist(p);
   #ifdef INTERP
   var righthand;
   if(!stack_pop(p->s,&righthand)){
      ERROR("Nothing left in the stack");
   }
   checkint(&righthand);
   p->v[lefthand]=righthand;

   if(!stack_free(p->s)){
      ERROR("Stack not freed");
   } 
   #endif
   return;   
}

void polishlist(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(strsame(p->wds[p->cw], ";")){
      return;
   }   
   if(!polish(p->wds[p->cw])){
      ERROR("Invalid POLISHLIST");
   }
   #ifdef INTERP
   if(pushdown(p->wds[p->cw])){
      i_pushdown(p);
   }
   else if(unaryop(p->wds[p->cw])){
      i_uop(p);
   }
   else if(binaryop(p->wds[p->cw])){
      i_bop(p);
   }
   #endif  
   p->cw = p->cw + 1;
   polishlist(p);
   return;
}

void ones(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(!integer(p->wds[p->cw])){
      ERROR("Invalid CREATE Statement");
   }
   #ifdef INTERP
   int row=atoi(p->wds[p->cw]);
   #endif
   p->cw = p->cw + 1;
   if(!integer(p->wds[p->cw])){
      ERROR("Invalid CREATE Statement");
   }
   #ifdef INTERP
   int col=atoi(p->wds[p->cw]);
   #endif   
   p->cw = p->cw + 1;
   if(!varname(p->wds[p->cw])){
      ERROR("Invalid CREATE Statement");
   }
   #ifdef INTERP
   int var=(int)(p->wds[p->cw][1]-'A');
   #ifdef EXTENSION
   if(p->v[var].isindex){
      ERROR("Cannot access loop index");
   }
   #endif
   p->v[var].row=row;
   p->v[var].col=col;
   for(int i=0;i<row;i++){
      for(int j=0;j<col;j++){
         p->v[var].matrix[i][j]=1;
      }
   }
   checkint(p->v+var);
   #endif
   return;  
}

void read(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(!string(p->wds[p->cw])){
      ERROR("Invalid CREATE Statement");
   }
   #ifdef INTERP
   char fname[MAXTOKENSIZE]="\n";
   extractstr(fname,p->wds[p->cw]);
   #endif
   p->cw = p->cw + 1;
   if(!varname(p->wds[p->cw])){
      ERROR("Invalid CREATE Statement");
   }
   #ifdef INTERP
   int var=(int)(p->wds[p->cw][1]-'A');

   #ifdef EXTENSION
   if(p->v[var].isindex){
      ERROR("Cannot access loop index");
   }
   #endif
   FILE* fp=nfopen(fname, "r");
   int row=0;
   int col=0;
   if(fscanf(fp,"%d %d\n",&row,&col)!=2){
      ERROR("Invalid .arr file");
   }
   if(row<=0||col<=0){
      ERROR("Invalid .arr file");
   }
   p->v[var].row=row;
   p->v[var].col=col;
   for(int i=0;i<row;i++){
      for(int j=0;j<col;j++){
         if(j==col-1){
            if(fscanf(fp,"%d\n",&(p->v[var].matrix[i][j]))!=1){
               ERROR("Invalid .arr file");
            }
         }
         else{
            if(fscanf(fp,"%d ",&(p->v[var].matrix[i][j]))!=1){
               ERROR("Invalid .arr file");
            }
         }
         
      }
   }
   fclose(fp);
   checkint(p->v+var);
   #endif
   return;
}

void loop(Program*p)
{
   if(!p){
      ERROR("NULL Program");
   }
   if(!varname(p->wds[p->cw])){
      ERROR("Invalid LOOP Statement");
   }
   #ifdef INTERP
   int loopvar=(int)(p->wds[p->cw][1]-'A');
   p->v[loopvar]=inttovar(1);
   p->v[loopvar].isint=true;
   #ifdef EXTENSION
   p->v[loopvar].isindex=true;
   #endif
   #endif
   p->cw = p->cw + 1;
   if(!integer(p->wds[p->cw])){
      ERROR("Invalid LOOP Statement");
   }
   #ifdef INTERP
   int index=atoi(p->wds[p->cw]);
   if(index==0){
      ERROR("LOOP index starts form 1");
   }
   #endif 
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw],"{")){
      ERROR("Invalid LOOP Statement");
   }
   p->cw = p->cw + 1;
   
   #ifdef INTERP
   int start=p->cw;
   while(p->v[loopvar].matrix[0][0]<=index){
      p->cw=start;
      instrclist(p);
      p->v[loopvar].matrix[0][0]++;
   }
   #ifdef EXTENSION
   p->v[loopvar].isindex=false;
   #endif
   #else
   instrclist(p);
   #endif
   return;
}

bool varname(char* str)
{
   if(!str){
      return false;
   }

   if(strlen(str)!=2){
      return false;
   }
   if(str[0]!='$'){
      return false;
   }
   if(str[1]>='A'&&str[1]<='Z'){
      return true;
   }
   return false;
}

bool string(char* str)
{
   if(!str){
      return false;
   }
   if(strlen(str)<2){
      return false;
   }   
   if(str[0]=='\"'&& str[strlen(str)-1]=='\"'){
      return true;
   }
   return false;
}

bool polish(char*str)
{
   if(!str){
      return false;
   }
   if(pushdown(str)||unaryop(str)||binaryop(str)){
      return true;
   }
   return false;
}

bool pushdown(char*str)
{
   if(!str){
      return false;
   }
   if(varname(str)||integer(str)){
      return true;
   }
   return false;
}

bool integer(char* str)
{
   if(!str){
      return false;
   }
   if(strlen(str)<=0){
      return false;
   }
   for(int i =0; i<(int)strlen(str);i++){
      if(str[i]>'9'||str[i]<'0')
      return false;
   }
   return true;
}

bool unaryop(char*str)
{
   if(!str){
      return false;
   }
   if(strsame(str,"U-NOT")||strsame(str,"U-EIGHTCOUNT")){
      return true;
   }
   return false;
}

bool binaryop(char*str)
{
   if(!str){
      return false;
   }
   if(strsame(str,"B-AND")||strsame(str,"B-OR")||strsame(str,"B-GREATER")||strsame(str,"B-LESS")||strsame(str,"B-ADD")||strsame(str,"B-TIMES")||strsame(str,"B-EQUALS")){
      return true;
   }
   return false;
}

#ifdef INTERP

void extractstr(char* dest, char*src){
   int l=strlen(src);
   for(int i=1;i<l-1;i++){
      dest[i-1]=src[i];
   }
}

void i_pushdown(Program*p)
{
   if(integer(p->wds[p->cw])){
      var new=inttovar(atoi(p->wds[p->cw]));
      stack_push(p->s,new);
   }
   else{
      int var =(int)(p->wds[p->cw][1]-'A');
      if(p->v[var].row==0||p->v[var].col==0){
         ERROR("Undefined Variable in Polihlist");
      }
      stack_push(p->s,p->v[var]);
   }
   return;
}

void i_uop(Program*p)
{
   if(strsame(p->wds[p->cw],"U-NOT")){
      var a;
      if(!stack_pop(p->s,&a)){
         ERROR("No Variable in the Stack");
      }
      if(!u_not(&a)){
         ERROR("Undefined Variable in Polihlist");
      }
      stack_push(p->s,a);
   }
   else if(strsame(p->wds[p->cw],"U-EIGHTCOUNT")){
      var a,b={{{0}},0,0,false,false};
      if(!stack_pop(p->s,&a)){
      ERROR("No Variable in the Stack");
      }
      if(!u_eightcount(&a,&b)){
         ERROR("Undefined Variable in Polihlist");
      }
      stack_push(p->s,b);
   }
   return;  
}

void i_bop(Program*p)
{ 
   var a,b,c={{{0}},0,0,false,false};
   if(!stack_pop(p->s,&b)){
      ERROR("No Variable in the Stack");
   }
   if(!stack_pop(p->s,&a)){
      ERROR("No Variable in the Stack");
   }
   if(a.col<=0||a.row<=0||b.col<=0||b.row<=0){
      ERROR("Undefined Variable");  
   }
   if(!match(&a,&b)){
      ERROR("Non-matching array size"); 
         
   }
   
   _bop(&a,&b,&c,p->wds[p->cw]);
   
   stack_push(p->s,c);
   return;
}

#ifdef EXTENSION

bool boolop(char*str){
   if(!str){
      return false;
   }
   if(strsame(str,"B-AND")||strsame(str,"B-OR")||strsame(str,"B-GREATER")||strsame(str,"B-LESS")||strsame(str,"B-EQUALS")){
      return true;
   }
   return false;
}

bool scoping(Program*p){
   int left=1,right=0;
   while(p->wds[p->cw]){
      if(strsame(p->wds[p->cw],"}")){
         right++;
      }
      if(strsame(p->wds[p->cw],"{")){
         left++;
      }
      if(left==right){
         return true;
      }
      p->cw++;
   }
   return false;
}

void ifstmt(Program *p){
   var a, b,c={{{0}},0,0,false,false};
   if(!p){
      ERROR("NULL Program");
   }
   if(!strsame(p->wds[p->cw],"(")){
      ERROR("Invalid IF Statement");
   }
   p->cw = p->cw + 1;

   if(!pushdown(p->wds[p->cw])){
      ERROR("Invalid IF Statement");
   }
   if(varname(p->wds[p->cw])){
      int vara=(int)(p->wds[p->cw][1]-'A');
      a=p->v[vara];
   }
   else{
      a=inttovar(atoi(p->wds[p->cw]));
   }
   p->cw = p->cw + 1;

   if(!pushdown(p->wds[p->cw])){
      ERROR("Invalid IF Statement");
   }
   if(varname(p->wds[p->cw])){
      int varb=(int)(p->wds[p->cw][1]-'A');
      b=p->v[varb];
   }
   else{
      b=inttovar(atoi(p->wds[p->cw]));
   }
   p->cw = p->cw + 1;

   if(!boolop(p->wds[p->cw])){
      ERROR("Invalid IF Statement");
   }
   if(!a.isint||!b.isint){
      ERROR("Invalid variable in the condition of IF statement");
   }
   _bop(&a,&b,&c,p->wds[p->cw]);
   p->cw = p->cw + 1;

   if(!strsame(p->wds[p->cw],")")){
      ERROR("Invalid IF Statement");
   }
   p->cw = p->cw + 1;

   if(!strsame(p->wds[p->cw],"{")){
      ERROR("Invalid IF Statement");
   }
   p->cw = p->cw + 1;
   
   if(c.matrix[0][0]==0){
      if(!scoping(p)){
         ERROR("Missing } or { ?");
      }
      return;
   }
   else{
      instrclist(p);
   } 
   return;
}
#endif
#endif





