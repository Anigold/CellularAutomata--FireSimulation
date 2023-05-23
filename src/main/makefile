COMPILER = javac

TARGET = Driver

clean	: 
			rm ./*.class 
			rm ./*/*.class
			rm ./*/*/*.class

run		:	$(TARGET).class
			java $(TARGET).java 350

compile	: 	$(TARGET).java
			javac $(TARGET).java

full 	:	$(TARGET).java
			rm ./*.class 
			rm ./*/*.class
			rm ./*/*/*.class
			javac $(TARGET).java
			java $(TARGET).java 350