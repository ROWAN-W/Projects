#include "nlab.h"

void printvar(const var* v){
   for(int i=0;i<v->row;i++){
      for(int j=0;j<v->col;j++){
         printf("%d ",v->matrix[i][j]);
      }
      printf("\n");
   }
}

bool checkint(var* v){
   if(v->row==1&&v->col==1){
      v->isint=true;
      return true;
   }
   return false;

}

bool match(var*a,var*b){ 
   if(a->isint){
      for(int i=0;i<b->row;i++){
         for(int j=0;j<b->col;j++){
            a->matrix[i][j]=a->matrix[0][0];
         }
      }
      a->row=b->row;
      a->col=b->col;
      a->isint=false;
      return true;
   }
   else if(b->isint){
      for(int i=0;i<a->row;i++){
         for(int j=0;j<a->col;j++){
            b->matrix[i][j]=b->matrix[0][0];
         }
      }
      b->row=a->row;
      b->col=a->col;
      b->isint=false;
      return true;
   }
   else if(a->row==b->row&&a->col==b->col){
       return true;
   }
   else{
       return false;
   }
}

//white-box testing
//convert an integer to a var
var inttovar(int x)
{
   var n={{{0}},1,1,true,false};
   n.matrix[0][0]=x;
   return n;
}


//white-box testing
//Detects Undefined Variable
bool u_not(var* a){
   if(a->col<=0||a->row<=0){
      return false;  
   }
   for(int i=0;i<a->row;i++){
      for(int j=0;j<a->col;j++){
	      if(a->matrix[i][j]!=0){
            a->matrix[i][j]=0; 
         }
         else{
	         a->matrix[i][j]=1;
         }
      }

   }
   return true;
}

bool u_eightcount(var*a,var*b)
{
   if(a->col<=0||a->row<=0){
      return false;  
   }
   b->row=a->row;
   b->col=a->col;
   for(int i=0;i<a->row;i++){
      for(int j=0;j<a->col;j++){
         int count=0;
         if(i-1>=0){
            if(a->matrix[i-1][j]){
               count++;
            }
         }
         if(i+1<a->row){
            if(a->matrix[i+1][j]){
            count++;
            }
         }
         if(j-1>=0){
            if(a->matrix[i][j-1]){
               count++;
            }
         }
         if(j+1<a->col){
            if(a->matrix[i][j+1]){
               count++;
            }
         }
         if(i-1>=0&&j-1>=0){
            if(a->matrix[i-1][j-1]){
               count++;
            }
         }
         if(i+1<a->row&&j-1>=0){
            if(a->matrix[i+1][j-1]){
               count++;
            }
         }
         if(i-1>=0&&j+1<a->col){
            if(a->matrix[i-1][j+1]){
               count++;
            }
         }
         if(i+1<a->row&&j+1<a->col){
            if(a->matrix[i+1][j+1]){
               count++;
            }
         }
         b->matrix[i][j]=count;
      }
   }
   return true;
}

//black-box testing
//actual calculation of B-OPs
void _bop(var*a,var*b,var*c,char* op){
   c->row=a->row;
   c->col=a->col;
   for(int i=0;i<a->row;i++){
      for(int j=0;j<a->col;j++){
         int cell=0;
         if(strsame(op,"B-AND")){
            if(a->matrix[i][j]&&b->matrix[i][j]){
               cell=1;
            }
         }
         else if(strsame(op,"B-OR")){
            if(a->matrix[i][j]||b->matrix[i][j]){
               cell=1;
            }
         }
         else if(strsame(op,"B-GREATER")){
            if(a->matrix[i][j]>b->matrix[i][j]){
               cell=1;
            }
         }
         else if(strsame(op,"B-LESS")){
            if(a->matrix[i][j]<b->matrix[i][j]){
               cell=1;
            }
         }
         else if(strsame(op,"B-ADD")){           
            cell=a->matrix[i][j]+b->matrix[i][j];        
         }
         else if(strsame(op,"B-TIMES")){
            cell=a->matrix[i][j]*b->matrix[i][j];
         }
         else if(strsame(op,"B-EQUALS")){
            if(a->matrix[i][j]==b->matrix[i][j]){
               cell=1;
            }
         }
         else{
            ERROR("Invalid Operator");
         }
         c->matrix[i][j]=cell;
      }
   }
}