//general.h, general.c, stack.h, linked.c are from Neill's codes and modified

#include "stack/stack.h"

#define  VARIABLENUM 26

#define MAXNUMTOKENS 1000000
#define MAXTOKENSIZE 100
#define strsame(A,B) (strcmp(A, B)==0)
#define ERROR(PHRASE) { fprintf(stderr, \
           "Fatal Error %s occurred in %s, line %d\n", PHRASE, \
           __FILE__, __LINE__); \
           exit(EXIT_FAILURE); }


struct prog{
   char wds[MAXNUMTOKENS][MAXTOKENSIZE];
   int cw; // Current Word#
   
   var v[VARIABLENUM];
   stack* s;
};
typedef struct prog Program;

//White-box tested with testcases
void prog(Program*p);
void instrclist(Program*p);//recursive
void instrc(Program*p);
void print(Program*p);
void set(Program*p);
void ones(Program*p);
void read(Program*p);
void loop(Program*p);//recursice
void polishlist(Program*p);//recursive

//Tested with assertions in black-box testing style
bool varname(char*str);
bool string(char*str);
bool polish(char*str);
bool pushdown(char*str);
bool integer(char*str);

//Tested with assertions in white-box testing style
bool unaryop(char*str);
bool binaryop(char*str);

#ifdef INTERP

//Black-box testing
//Extract filename from string with double quotes
void extractstr(char* dest, char*src);

//Detects undefined variables
//push one var down
void i_pushdown(Program*p);

//Pop one var, push one dwon
void i_uop(Program*p);

//White-box testing
//Pops two variables, push down one
//Detects Undefined Variable and Non-matching array size
void i_bop(Program*p);


#ifdef EXTENSION
//White-box testing with assertions
bool boolop(char*str);
//White-box tested with testcases 
bool scoping(Program*p);
void ifstmt(Program *p);

#endif

#endif

void test(void);
