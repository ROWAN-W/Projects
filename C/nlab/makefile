
CFLAGS := -Wall -Wextra -Wpedantic -Wfloat-equal -Wvla -std=c99
SANI := -fsanitize=undefined -fsanitize=address
NLBS :=$(wildcard*.nlb)
RESULTS :=$(NLBS:.nlb=.result)

#general.h, general.c, stack.h and linked.c are from Neill's codes and modified

all: parse parse_s parse_v interp interp_s interp_v  extension extension_s  extension_v

parse: nlab.c nlab.h 
	gcc nlab.c general/general.c $(CFLAGS) -O2 -o parse -lm

parse_s: nlab.c nlab.h
	gcc nlab.c general/general.c $(CFLAGS) $(SANI) -g3 -o parse_s -lm

parse_v: nlab.c nlab.h 
	gcc nlab.c general/general.c $(CFLAGS) -g3 -o parse_v -lm

interp: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) -O2 -DINTERP -o interp -lm

interp_s: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) $(SANI) -g3 -DINTERP -o interp_s -lm

interp_v: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) -g3 -DINTERP -o interp_v -lm

extension: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) -O2 -DINTERP -DEXTENSION -o extension -lm

extension_s: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) $(SANI) -g3 -DINTERP -DEXTENSION -o extension_s -lm

extension_v: nlab.c nlab.h var.c var.h
	gcc nlab.c var.c general/general.c stack/linked.c $(CFLAGS) -g3 -DINTERP -DEXTENSION -o extension_v -lm





#for all .nlb files, run them and output result to a .result file
runall:
	$(RESULTS)

%.result:
	./interp $*.nlb > $*.RESULTS

clean: 
	rm -f parse parse_s parse_v interp interp_s interp_v extension extension_s extension_v $(RESULTS)

