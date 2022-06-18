#include "../dict.h"
#include <ctype.h>
#define MAXWORD 100
#define FACTOR_EXACT 2
struct word{
   char w[MAXWORD];
   struct word* next;
};

typedef struct word word;

struct dict{
   word* table;
   unsigned int sz;
};


void test(void);
bool _checkstr(const char*s);
bool _isin(const dict* d, const char *s);
int _hash(unsigned int sz,const char *s);

