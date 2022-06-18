#include "../dict.h"
#include <ctype.h>

#define FACTOR_APPROX 20
#define KHASHES 11

struct dict{
   bool* table;
   unsigned long sz;
};

void test(void);
bool _checkstr(const char*s);
bool _isin(const dict* d, const char *s);
unsigned long _hash(const char *s);
unsigned long * _hashes(unsigned long sz,const char * s);
