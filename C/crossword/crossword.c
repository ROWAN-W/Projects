#include "crossword.h"
#define WHITE 'w'
#define BLACK 'b'

bool checkstr(int sz, char* ip);
bool downclu(int i,int j, const crossword* cw);
bool acrossclu(int i,int j,const crossword* cw);
bool columnclu(int i, int j, const crossword* cw);
bool rowclu(int i, int j, const crossword* cw);
int emptynum(const crossword* cw);

// Might be useful to be able to print them
// to help with debugging
void print_crossword(const crossword* c)
{
   for(int y=0; y<c->sz; y++){
      for(int x=0; x<c->sz; x++){
         printf("%c", c->arr[y][x]);
      }
      printf("\n");
   }
}

bool str2crossword(int sz, char* ip, crossword* cw)
{/*
   Convert a size (sz) & string (ip) into a crossword*
   White squares are ' ' or '.', black squares 'X' or '*'
*/
   if(!checkstr(sz,ip)){
      return false;
   }
   if(cw==NULL){
      return false;
   }

   cw->sz=sz;

   for(int i=0;i<sz;i++) {
      for(int j=0;j<sz;j++){
         if(ip[(sz*i+j)]==' '||ip[(sz*i+j)]=='.'){
            cw->arr[i][j]=WHITE;
         }
         else{
            cw->arr[i][j]=BLACK;
         }

      }
   }
   return true;

}

bool checkstr(int sz, char* ip)
{
   if(sz<=0||sz>=GRID){
      return false;
   }

   if(ip==NULL){
      return false;
   }
   
   int cnt=0;
   for(int i=0;i<(int)strlen(ip);i++){
      if(ip[i]==' '||ip[i]=='.'||ip[i]=='X'||ip[i]=='*'){
         cnt++;
      }
   }
   if(cnt!=sz*sz){
      return false;
   }
   return true;
}

int emptynum(const crossword* cw){
   int cnt=0;
   for(int i=0;i<cw->sz;i++){
      for(int j=0;j<cw->sz;j++){
         if(cw->arr[i][j]==WHITE){
            cnt++;
         }
      }
   }
   
   return cnt;
}



int getchecked(crossword c)
{  //Get percentage of empty squares that are shared between two clues
   //squares which are shared by both an across clue and a down clue,
   //meaning there will be a down clue in its column, and an across clue in its row
   int cnt=0;
   for(int i=0;i<c.sz;i++){
      for(int j=0;j<c.sz;j++){
         if(c.arr[i][j]==WHITE){
            if(rowclu(i,j,&c) && columnclu(i,j,&c)){
               cnt++;                
            }
         }          

      }
   }
   double percentage=(double)cnt/(double)emptynum(&c);
return (int)round(100*percentage);
}

void getcluestring(const crossword* c, char* ans)
{  char across[GRID]="A";
   char down[GRID]="D";
   int cnt=0;
   
   for(int i=0;i<c->sz;i++){
      for(int j=0;j<c->sz;j++){
         if(downclu(i,j,c)){
            cnt++;
            char s[GRID];
            sprintf(s,"-%i",cnt);
            if(acrossclu(i,j,c)){               
               strcat(down,s);
               strcat(across,s);
            }
            else{
               strcat(down,s);
            }

         }
         else if(acrossclu(i,j,c)&&!downclu(i,j,c)){
            cnt++;
            char s[GRID];
            sprintf(s,"-%i",cnt);
            strcat(across,s);
         }
      }
   }
   
   sprintf(ans,"%s|%s",across,down);
   
}





bool downclu(int i,int j, const crossword* cw)
{
   if(cw->arr[i][j]==WHITE &&i==0 && cw->arr[i+1][j]==WHITE){
      return true;
   }
   else if(cw->arr[i][j]==WHITE &&cw->arr[i-1][j]==BLACK && cw->arr[i+1][j]==WHITE){
      return true;
   }
   else{
      return false;
   }
}

bool acrossclu(int i,int j,const crossword* cw){
   if(cw->arr[i][j]==WHITE &&j==0 && cw->arr[i][j+1]==WHITE){
      return true;
   }
   else if(cw->arr[i][j]==WHITE && cw->arr[i][j-1]==BLACK && cw->arr[i][j+1]==WHITE){
      return true;
   }
   else{
      return false;
   }
}

bool columnclu(int i, int j, const crossword* cw)
{   
   for(int y=0;y<i+1;y++){
      if(downclu(i-y,j,cw)){
         return true;
      }
      else if(cw->arr[i-y][j]==BLACK){
         return false;
      }
   }
   return false;
}

bool rowclu(int i, int j,const crossword* cw)
{   
   for(int x=0;x<j+1;x++){
      if(acrossclu(i,j-x,cw)) {
         return true;
      }
      else if(cw->arr[i][j-x]==BLACK){
         return false;
      }
   }
   return false;
}

void test(void)
{
   crossword c;
   //incorrect size
   assert(!checkstr(0,"..***XX"));
   //invalid string
   assert(!checkstr(2,"fhusbofbdots"));  
   //crossword in exercise 4.1 && tesing char' '
   str2crossword(5, ".. .X.XX.X.X.... .X.XX...", &c);
   //test downclu()
   assert(downclu(0,0,&c));
   assert(downclu(2,4,&c));
   assert(!downclu(3,0,&c));
   //test acrossclu()
   assert(acrossclu(0,0,&c));
   assert(acrossclu(4,2,&c));
   assert(!acrossclu(0,3,&c));
   //test columnclu()
   assert(columnclu(1,0,&c));
   assert(!columnclu(0,1,&c));
   //test rowclu()
   assert(rowclu(3,1,&c));
   assert(!rowclu(1,3,&c));
   //test emptynum()
   assert(emptynum(&c)==17);
}
