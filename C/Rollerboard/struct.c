#include <stdio.h>

struct t{
 char arr[2][2];
  int size;

};
typedef struct t t;
int main(void){
  t t1, t2, *t3,*t4;
   t1.arr[1][1]='2';
   t1.size=2;
   t3=&t1;
   t4=&t2;
   *t4=*t3;
   printf("%c",t2.arr[1][1]);
   



return 0;


}
