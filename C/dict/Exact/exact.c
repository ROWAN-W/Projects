#include "specific.h"

void test(void)
{
   assert(!_checkstr("a-1"));
   assert(_checkstr("oe"));
   assert(!_checkstr("0"));

   dict* d=(dict*)ncalloc(1,sizeof(dict));
   d->sz=100;
   d->table=(word*)ncalloc(d->sz,sizeof(word));

   dict_add(d, "one");
   dict_add(d, "one");
   dict_add(d, "two");

   assert(_isin(d,"one"));
   assert(!_isin(d,"ONE"));
   assert(_isin(d,"two"));

   dict_free(d);
}


bool _checkstr(const char*s)
{
   for(int i=0;i<(int)strlen(s);i++){
      if(!isalpha(s[i])){
         return false;
      }
   }
   return true;
}

bool _isin(const dict* d, const char *s)
{
   if(!d||!s||!d->table){
      return false;
   }
   int h=_hash(d->sz,s);
   word* p=d->table+h;
   while(p){
      if(!strcmp(p->w,s)){
         return true;
      }
      p=p->next;
   }
   return false;
}

/*Modified Bernstein hashing
5381 & 33 are magic numbers required by the algorithm
*/
int _hash(unsigned int sz,const char *s)
{
   unsigned long hash =5381;
   int c;
   while((c=(*s++))){
      hash=33*hash^c;
   }
   return (int)(hash%sz);
}





/* The maximum number of words we will want to input.
   Exact : Hashtable will be twice this size
   Approx : Hashtable will be (e.g.) 20 times this size
*/
dict* dict_init(unsigned int maxwords)
{ 
   test();
   dict* d=(dict*)ncalloc(1,sizeof(dict));
   d->sz=FACTOR_EXACT*maxwords;
   d->table=(word*)ncalloc(d->sz,sizeof(word));
   return d;
}
/* Add string to dictionary
   Exact : A deep-copy is stored in the hashtable only if the word
           has not already been added to the table.
   Approx : Multiple hashes (e.g. 11) are computed and corresponding
            Boolean flags set in the Bloom hashtable. 
*/
bool dict_add(dict* x,  const char* s)
{
   if(x==NULL||x->table==NULL||x->sz==0||s==NULL){
      return false;
   }
   if(!_checkstr(s)){
      return false;
   }   
   if(_isin(x,s)){
      return true;
   }

   int h=_hash(x->sz,s);
   if(x->table[h].w[0]=='\0' ){
      strcpy(x->table[h].w,s);
      return true;
   }
   
   word* p=x->table+h;
   while(p->next){
      p=p->next;
   }  
   p->next=(word*)ncalloc(1,sizeof(word));   
   strcpy(p->next->w,s);

   return true;
}
/* Returns true if the word is already in the dictionary,
   false otherwise.
*/
bool dict_spelling(dict* x, const char* s)
{
   if(_isin(x,s)){
      return true;
   }
   return false;
}
/* Frees all space used */
void dict_free(dict* x)
{ 
   for(int i=0;i<(int)x->sz;i++){
      word *p,*f;
      p=x->table+i;
      f=p->next;
      while(f){
         p=f->next;
         free(f);
         f=p;
      }
   }
   
   free(x->table);
   free(x);
}
