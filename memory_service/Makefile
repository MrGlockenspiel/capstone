JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
JAR = /usr/bin/jar

BIN := $(notdir $(PWD)).jar

SRC_DIR = src
BUILD_DIR = build
MAIN_CLASS = Main

.PHONY: all run clean

all:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(wildcard $(SRC_DIR)/*.java)
	echo "Main-Class: $(MAIN_CLASS)" > manifest.txt
	$(JAR) cfm $(BIN) manifest.txt -C $(BUILD_DIR) .
	rm manifest.txt

run: all
	$(JAVA) -jar $(BIN)

clean:
	rm -rf $(BUILD_DIR) $(BIN) manifest.txt
