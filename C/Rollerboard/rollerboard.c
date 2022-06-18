#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <stdbool.h>

#define BOARD_SIZE 6
#define BIGSTR  1000
#define LINE 100
#define BIGARRAY 100000



struct board{
   char bd[BOARD_SIZE][BOARD_SIZE];
   int rows;
   int columns;
   int parent; 
};
typedef struct board board;

struct bdlist{
   board* list[BIGARRAY];
   int sz;
   int rows;
   int columns;
};
typedef struct bdlist bdlist;

//can't assert readfile() because the file I'm using may not exist in test directory 
char* readfile(char* fname, char str[BIGSTR]);
board* getbd(char str[BIGSTR]);
void printbd(const board* cell);

void colup(int j,board* temp);
void coldown(int j,board* temp);
void rowleft(int j, board* temp);
void rowright(int j, board* temp);

board* play(bdlist* rollerbd);
void addbd(bdlist* rollerbd,int i,board* temp);

bool success(board* cell);
bool unique(const bdlist* rollerbd, const board* temp);

void result(const bdlist* rollerbd, board* end);
void verbose(const bdlist* rollerbd,board* end);
void freelist(bdlist* rollerbd);


void test(void)
{
   bdlist rollerbd;
   rollerbd.sz=0;
   char str[BIGSTR]="5 4\n0000\n1100\n1000\n0100\n0000\n";

   rollerbd.list[0]=getbd(str);
   assert(rollerbd.list[0]->bd[1][1]=='1');
   assert(rollerbd.list[0]->bd[1][2]=='0');
   rollerbd.sz++;
   rollerbd.rows=rollerbd.list[0]->rows;
   rollerbd.columns=rollerbd.list[0]->columns;
   
   colup(0,rollerbd.list[0]);
   assert(rollerbd.list[0]->bd[0][0]=='1');
   
   
   coldown(4,rollerbd.list[0]);
   assert(rollerbd.list[0]->bd[0][0]=='0');

   rowleft(9,rollerbd.list[0]);
   assert(rollerbd.list[0]->bd[1][1]=='0');
   
   rowright(14,rollerbd.list[0]);
   assert(rollerbd.list[0]->bd[1][1]=='1');
   
   play(&rollerbd);

   assert(success(rollerbd.list[rollerbd.sz-2])==false);
   assert(success(rollerbd.list[rollerbd.sz-1])==true);
  
   assert(unique(&rollerbd,rollerbd.list[0])==false);
   assert(unique(&rollerbd,rollerbd.list[3])==false);

   freelist(&rollerbd);

}









int main(int argc,char*argv[])
{  
   //test();

   bool v=false;
   bdlist rollerbd;
   rollerbd.sz=0;
   //it's crucial to initialize the string
   char str[BIGSTR]="";
   if(argc==2){
      readfile(argv[1],str);
      rollerbd.list[0]=getbd(str);
      rollerbd.sz++;
      rollerbd.rows=rollerbd.list[0]->rows;
      rollerbd.columns=rollerbd.list[0]->columns;
   }
    
   if(argc==3){
      if(strcmp(argv[1],"-v")!=0){
         fprintf(stderr,"use flag -v to see the solution path\n");
         exit(EXIT_FAILURE);
      }
      v=true;
      readfile(argv[2],str);
      rollerbd.list[0]=getbd(str);
      rollerbd.sz++;
      rollerbd.rows=rollerbd.list[0]->rows;
      rollerbd.columns=rollerbd.list[0]->columns;
   }
   
   board* ptr=play(&rollerbd);
   if(ptr==NULL){
      fprintf(stderr,"fail in playing\n");
      exit(EXIT_FAILURE);
   }
   if(v==false){
      result(&rollerbd,ptr);
   }
   else if(v==true){
      verbose(&rollerbd,ptr);
   }
   

   freelist(&rollerbd);

   return 0;

}

char* readfile(char* fname, char str[BIGSTR])
{
   FILE* fbd=fopen(fname, "r");
   if(fbd==NULL){
      fprintf(stderr,"Cannot open file %s\n",fname);
      exit(EXIT_FAILURE);
   }
   char line[LINE];

   while(fgets(line, LINE,fbd)!=NULL)
   {
      strcat(str,line);
   }
   fclose(fbd);
   return str;
}

board* getbd(char str[BIGSTR])
{ 
   board* initbd=calloc(1,sizeof(board));

   if(initbd==NULL){
      fprintf(stderr,"Cannot create 1st board\n");
      exit(EXIT_FAILURE);
   }
   initbd->parent=0;  
   
   if(sscanf(str,"%d %d\n",&(initbd->rows),&(initbd->columns))!=2 ){
      fprintf(stderr,"fail to read in board features\n");
      exit(EXIT_FAILURE);
   }

   
   if(initbd->rows>6||initbd->rows<0||initbd->columns>6||initbd->columns<0){
      fprintf(stderr,"invalid board size\n");
      exit(EXIT_FAILURE);
   }

   
   int ones=0, zeros=0, sum=0;
   for(int i=0; i<initbd->rows;i++){
      for(int j=0;j<initbd->columns;j++){
         if(str[4+i*(initbd->columns+1)+j]=='0'){
            initbd->bd[i][j]='0';
            zeros++;
         }
         else if(str[4+i*(initbd->columns+1)+j]=='1'){
            initbd->bd[i][j]='1';
            ones++;
         }
      }
   }
   sum=ones+zeros;

   if(ones!=initbd->columns || sum!=initbd->rows*initbd->columns){
      fprintf(stderr,"invalid board\n");
      exit(EXIT_FAILURE);
   }
   
   
   return initbd;  
}


void printbd(const board* cell){
    for(int i=0; i<cell->rows;i++){     
     printf("%s\n",cell->bd[i]);
   }
}

void addbd(bdlist* rollerbd,int i,board* temp)
{
   board* newbd=calloc(1,sizeof(board));
   if(newbd==NULL){
      fprintf(stderr,"fail to create new board\n");
      exit(EXIT_FAILURE);
   }

   *newbd=*temp;
   newbd->parent=i;
   if(rollerbd->sz>=BIGARRAY-1){
      fprintf(stderr,"No Solution?\n");
      exit(EXIT_FAILURE);
   }
   rollerbd->list[rollerbd->sz]=newbd;
   rollerbd->sz++;
}


board* play(bdlist* rollerbd)
{  
   if(rollerbd->list[0]==NULL){
      fprintf(stderr,"fail to read the original board\n");
      exit(EXIT_FAILURE);
   }
   for(int i=0;i<BIGARRAY;i++){
      if(success(rollerbd->list[i])){
	   return rollerbd->list[i];
      }

      for(int j=0;j<(rollerbd->rows+rollerbd->columns)*2;j++){
         if(j>=0 && j<=rollerbd->columns-1){
            board temp=*rollerbd->list[i];
            colup(j,&temp);
            if(unique(rollerbd,&temp)){
               addbd(rollerbd,i,&temp);
               if(success(rollerbd->list[rollerbd->sz-1])){		  
                  return rollerbd->list[rollerbd->sz-1];
               }       
            }                     
         }        
         if(j>=rollerbd->columns && j<=rollerbd->columns*2-1){
            board temp=*rollerbd->list[i];
            coldown(j,&temp);
            if(unique(rollerbd,&temp)){  
               addbd(rollerbd,i,&temp);
               if(success(rollerbd->list[rollerbd->sz-1])){                 
                  return rollerbd->list[rollerbd->sz-1];
               }       
            }                     
         }
	 if(j>=rollerbd->columns*2 && j<=rollerbd->columns*2+rollerbd->rows-1){
            board temp=*rollerbd->list[i];
            rowleft(j,&temp);
            if(unique(rollerbd,&temp)){
               addbd(rollerbd,i,&temp);
               if(success(rollerbd->list[rollerbd->sz-1])){                  
                  return rollerbd->list[rollerbd->sz-1];
               }       
            }                     
         }
         if(j>=rollerbd->columns*2+rollerbd->rows && j<=rollerbd->columns*2+rollerbd->rows*2-1){
            board temp=*rollerbd->list[i];
            rowright(j,&temp);
            if(unique(rollerbd,&temp)){
               addbd(rollerbd,i,&temp);
               if(success(rollerbd->list[rollerbd->sz-1])){                 
                  return rollerbd->list[rollerbd->sz-1];
               }       
            }                     
         }
 
       }
   }
   fprintf(stderr,"No Solution?\n");
   exit(EXIT_FAILURE);
}

void colup(int j,board* temp)
{  
   char first=temp->bd[0][j];
   for(int i=0;i<temp->rows-1;i++){
      temp->bd[i][j]=temp->bd[i+1][j];
   }
   temp->bd[temp->rows-1][j]=first;
}

void coldown(int j,board* temp)
{
   j=j-temp->columns;
   char last=temp->bd[temp->rows-1][j];
   for(int i=temp->rows-1;i>0;i--){
      temp->bd[i][j]=temp->bd[i-1][j];
   }
   temp->bd[0][j]=last;
}
void rowleft(int j, board* temp)
{
   j=j-temp->columns*2;
   char first=temp->bd[j][0];
   
   for(int i=0;i<temp->columns-1;i++){
      temp->bd[j][i]=temp->bd[j][i+1];
   }
   temp->bd[j][temp->columns-1]=first;
}

void rowright(int j,board* temp){
   j=j-(temp->columns*2+temp->rows);
   char last=temp->bd[j][temp->columns-1];
   for(int i=temp->columns-1;i>0;i--){
      temp->bd[j][i]=temp->bd[j][i-1];
   }
   temp->bd[j][0]=last;
}

bool unique(const bdlist* rollerbd, const board* temp)
{  
   for(int i=0;i<rollerbd->sz;i++){
      int unique=0;
      for(int j=0;j<rollerbd->rows;j++){
         if(strcmp(rollerbd->list[i]->bd[j],temp->bd[j])==0){
            unique++;
         }
      }
      if(unique==rollerbd->rows){
         return false;
      }
   }
   
   return true;
}


bool success(board* cell)
{  
   for(int i=0;i<cell->rows;i++){
      for(int j=0;j<cell->columns;j++){
         if(i==0){
            if(cell->bd[i][j]!='1'){
               return false;
            }
         }
         else{
	   if(cell->bd[i][j]!='0'){
              return false;
           }
         }    
      }
   }
   return true;

}

void result(const bdlist* rollerbd, board* end)
{   
   board* ptr=end;
   int moves=1;
   while(ptr->parent!=0){
      ptr=rollerbd->list[ptr->parent];
      moves++;
   }
   
   printf("%d moves\n",moves);
}

void verbose(const bdlist* rollerbd,board* end)
{ 
   board* ptr=end;
   int moves=1;
   int path[BIGARRAY];
   while(ptr->parent!=0){
      path[moves]=ptr->parent;
      ptr=rollerbd->list[ptr->parent];
      moves++;
   }

   for(int i=moves;i>=1;i--){
      printf("%d:\n",moves-i);
      printbd(rollerbd->list[path[i]]);
      printf("\n");
   }

   printf("%d:\n",moves);
   printbd(rollerbd->list[rollerbd->sz-1]);
   printf("\n");
}

void freelist(bdlist* rollerbd){
   for(int i=0;i<rollerbd->sz;i++){
      free(rollerbd->list[i]);
   }
}
