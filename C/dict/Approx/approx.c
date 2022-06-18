#include "specific.h"


void test(void)
{
   assert(!_checkstr("a1aaaa"));
   assert(_checkstr("oeeee"));
   assert(!_checkstr("02"));

   dict* d=(dict*)ncalloc(1,sizeof(dict));
   d->sz=100;
   d->table=(bool*)ncalloc(d->sz,sizeof(bool));

   dict_add(d, "one");
   dict_add(d, "one");
   dict_add(d, "two");

   assert(_isin(d,"one"));
   assert(!_isin(d,"ONE"));
   assert(_isin(d,"two"));

   dict_free(d);
}

unsigned long _hash(const char *s)
{
   unsigned long hash =5381;
   int c;
   while((c=(*s++))){
      hash=33*hash^c;
   }
   return hash;
}



unsigned long * _hashes(unsigned long sz,const char * s)
{
// You’ll need to free this later
unsigned long * hashes = ncalloc(KHASHES, sizeof(unsigned long));
// Use Bernstein from Lecture Notes (or other suitable hash)
unsigned long bh = _hash(s);
int ln = strlen(s);
/* If two different strings have the same bh, then
we need a separate way to distiguish them when using
bh to generate a sequence */
srand(bh*(ln*s[0] + s[ln-1]));
unsigned long h2 = bh;
for (int i=0; i<KHASHES; i++) {
h2 = 33 * h2 ^ rand();
// Still need to apply modulus to these to fit table size
hashes[i] = h2%sz;
}
return hashes;
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

bool _isin(const dict* d,const char* s){
   if(!d||!s||!d->table){
      return false;
   }

   unsigned long *hs=_hashes(d->sz,s);
   for(int i=0;i<KHASHES;i++){
      if(d->table[hs[i]]!=1){
         free(hs);
         return false;
      }
   }
   free(hs);
   return true;
}


/* The maximum number of words we will want to input.
   Exact : Hashtable will be twice this size
   Approx : Hashtable will be (e.g.) 20 times this size
*/
dict* dict_init(unsigned int maxwords)
{  
   test();
   dict* d=(dict*)ncalloc(1,sizeof(dict));
   d->sz=FACTOR_APPROX*maxwords;
   d->table=(bool*)ncalloc(d->sz,sizeof(bool));
   
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

   unsigned long *hs=_hashes(x->sz,s);
   for(int i=0;i<KHASHES;i++){
      x->table[hs[i]]=1;
   }
   free(hs);
   return true;
}

/* Returns true if the word is already in the dictionary,
   false otherwise.
*/
bool dict_spelling(dict* x, const char* s){
   if(_isin(x,s)){
      return true;
   }
   return false;
}

/* Frees all space used */
void dict_free(dict* x)
{
   free(x->table);
   free(x);
}


