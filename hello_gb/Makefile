.PHONY: clean

all: hello.gb

hello.gb: hello.o
	rgblink -o hello.gb hello.o
	rgbfix -v -p 0xFF hello.gb

hello.o: hello.asm
	rgbasm -o hello.o hello.asm

clean:
	rm -f hello.o hello.gb