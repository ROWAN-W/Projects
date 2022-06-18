#include "general/general.h"
#define  MAXMATRIX  100
struct var{
    int matrix[MAXMATRIX][MAXMATRIX];
    int row;
    int col;
    bool isint;
    bool isindex;
};
typedef struct var var;

#ifdef INTERP

void printvar(const var*v);

//White-box testing with assert()
//Checks if var size equals 1x1
bool checkint(var* v);
//Checks the size of two vars 
bool match(var*a,var*b);

//White-box testing
//Tnitialize a var with integer x
var inttovar(int x);

//bool u_not(var* a) function(black-box testing)
//Checks if var a is bigger than size 0
bool u_not(var* a);

//bool u_eightcount(var* a) function(black-box testing)
//Check if var a is bigger than size 0
bool u_eightcount(var*a,var*b);

//Black-box tested with assert()
//Does B-ops on two vars
void _bop(var*a,var*b,var*c,char*op);

#endif