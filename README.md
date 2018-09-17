# FSA_validator

##Description
The program check the defined in input file FSA for errors, if no any, give a report if the FSA is complete/incomplete, yields warnings if any.

##Errors:
E1: A state s is not in set of states
E2: Some states are disjoint
E3: A transition a is not represented in the alphabet
E4: Initial state is not defined
E5: Input file is malformed
##Report:
FSA is complete/incomplete
##Warnings:
W1: Accepting state is not defined
W2: Some states are not reachable from initial state
W3: FSA is nondeterministic

##Input file format
states={s1,s2,...}	  // s1 , s2, ... ∈ latin letters, words and numbers
alpha={a1,a2, ...}	  // a1 , a2, ... ∈ latin letters, words, numbers and character '_’(underscore)
init.st={s}	  // s ∈ states
fin.st={s1,s2,...}	  // s1, s2 ∈ states
trans={s1>a>s2,... }	  // s1,s2,...∈ states; a ∈ alpha

#Example 1
fsa.txt
states={on,off}
alpha={turn_on,turn_off}    
init.st={off}
fin.st={}
trans={off>turn_on>off,on>turn_off>on}
result.txt
Error:
E2: Some states are disjoint
Example 2
fsa.txt
