import java.io.*;
import java.util.Arrays;
import java.util.Scanner;


public class Transaction {
    public static String E2 = "\nE2: Some states are disjoint";
    public static String E4 = "\nE4: Initial state is not defined";
    public static String E5 = "\nE5: Input file is malformed";

    public static String CompleteR = "FSA is complete";
    public static String IncompleteR = "FSA is incomplete";

    public static String W1 = "\nW1: Accepting state is not defined";
    public static String W2 = "\nW2: Some states are not reachable from initial state";
    public static String W3 = "\nW3: FSA is nondeterministic";

    public static String stub = "-123";

    public static boolean isE1 = false, isE2 = false, isE3 = false, isE4 = false, isE5 = false, isComp = true, isW1 = false, isW2 = false, isW3 = false;

    String title;
    String from;
    String to;

    public Transaction(String trans) {
        String from, title, to;
        String temp[] = trans.split(">");
        from = temp[0];
        title = temp[1];
        to = temp[2];

        this.from = from;
        this.title = title;
        this.to = to;
    }

    public Transaction() {
    }

    public static void main(String[] args) throws IOException {
        File inputFile = new File("fsa.txt");
        Scanner in = new Scanner(inputFile);
        Transaction t = new Transaction();
        String states[], alpha[], initState = "", finStates[], transactionsS[];

        //check error 5
        if (in.hasNext()) {
            //if(!in.nextLine().contains("states={") || !in.nextLine().contains("}")){isE5=true;}
            states = in.nextLine().replace("states={", "").replace("}", "").split(",");
            //if(!in.nextLine().contains("alpha={") || !in.nextLine().contains("}")){isE5=true;}
            alpha = in.nextLine().replace("alpha={", "").replace("}", "").split(",");
            //if(!in.nextLine().contains("init.st={") || !in.nextLine().contains("}")){isE5=true;}
            initState = in.nextLine().replace("init.st={", "").replace("}", "");
            //if(!in.nextLine().contains("fin.st={") || !in.nextLine().contains("}")){isE5=true;}
            finStates = in.nextLine().replace("fin.st={", "").replace("}", "").split(",");
            //if(!in.nextLine().contains("trans={")|| !in.nextLine().contains("}")){isE5=true;}
            transactionsS = in.nextLine().replace("trans={", "").replace("}", "").split(",");

            Transaction trans[] = new Transaction[transactionsS.length];
            for (int i = 0; i < transactionsS.length; i++) {
                trans[i] = new Transaction(transactionsS[i]);
            }

            File outputFile = new File("result.txt");         // declare FileWriter
            FileWriter fileWriter = new FileWriter(outputFile);
            PrintWriter out = new PrintWriter(fileWriter);

            t.checkError4(initState);
            String e3[] = t.checkError3(alpha, trans);
            String e1[] = t.checkError1(states, initState, finStates);
            t.checkError2(states, trans);

            //if errors exist, output the first was met
            //otherwise output the report(FSA is complete/incomplete) and all the warnings
            if (isE1 || isE2 || isE3 || isE4||isE5) {
                out.print("Error:");

                if(isE5){out.print(E5);}
                else {
                    for (int i = 0; i < e1.length; i++) {
                        if (!e1[i].equals(stub) && !e1[i].equals("")) {
                            out.print("\nE1: A state '" + e1[i] + "' is not in set of states");
                        }
                    }

                    if (isE2) {
                        out.print(E2);
                    }

                    for (int i = 0; i < e3.length; i++) {
                        if (!e3[i].equals(stub) && !e3[i].equals("")) {
                            out.print("\nE3: A transition '" + e3[i] + "' is not represented in the alphabet");
                        }
                    }

                    if (isE4) {
                        out.print(E4);
                    }
                }
            } else {
                t.makeReport(states, trans, alpha);
                if (isComp) {
                    out.print(CompleteR);
                } else {
                    out.print(IncompleteR);
                }

                t.checkWarning1(states, finStates);
                t.checkWarning2(states, trans);
                t.checkWarning3(states, alpha, trans);

                if (isW1 || isW2 || isW3) {
                    out.print("\nWarning:");
                    if (isW1) {
                        out.print(W1);
                    }
                    if (isW2) {
                        out.print(W2);
                    }
                    if (isW3) {
                        out.print(W3);
                    }
                }
            }
            fileWriter.close();
        }
        else  {File outputFile = new File("result.txt");         // declare FileWriter
        FileWriter fileWriter = new FileWriter(outputFile);
        PrintWriter out = new PrintWriter(fileWriter);
        out.print("Error:\n"+ E5);
        fileWriter.close();
    }}

    /**
     * Error 1: A state s is not in set of states
     * the method yields an error if initial state or any of state in the set of final states are not in the set of states
     * String out[] - an array of states not in the set of states
     */
    public String[] checkError1(String set[], String initSt, String finSt[]) {
        boolean initIsInSet = false;
        int n = 0;
        String out[] = new String[set.length + finSt.length];
        Arrays.fill(out, stub);
        for (int i = 0; i < set.length; i++) {
            if (initSt.equals(set[i])) {
                initIsInSet = true;
            }
        }
        if (!initIsInSet) {
            out[n] = initSt;
            n++;
        }
        boolean finIsInSet = false;

        for (int i = 0; i < finSt.length; i++) {
            for (int j = 0; j < set.length; j++) {
                if (finSt[i].equals(set[j])) {
                    finIsInSet = true;
                }
            }
            if (!finIsInSet) {
                out[n] = finSt[i];
                n++;
                finIsInSet = false;
            }
        }
        if (!out[0].equals(stub) && !out[0].equals("")) {
            isE1 = true;
        }
        return out;
    }

    /**
     * Error 2: Some states are disjoint
     * the method yields an error if there is at least 1 state that is disjoint( apart if we consider the fsa as undirected graph)
     */
    public void checkError2(String states[], Transaction trans[]) {

        if (states.length != 1) {
            String count[][] = new String[states.length][2];

            for (int i = 0; i < states.length; i++) {
                count[i][0] = states[i];
                count[i][1] = "0";  // count[i][0] for the number of outgoing and incoming links with other states
            }
            //counting the number of outgoing links with other states
            for (int i = 0; i < states.length; i++) {
                int k = 1;
                for (int j = 0; j < trans.length; j++) {
                    if (trans[j].from.equals(count[i][0]) && !trans[j].to.equals(count[i][0])) {
                        count[i][1] = Integer.toString(Integer.parseInt(count[i][1]) + 1);
                        k++;
                    }
                }
            }
            //counting the number of incoming links with other states
            for (int i = 0; i < states.length; i++) {
                int k = 1;
                for (int j = 0; j < trans.length; j++) {
                    if (trans[j].to.equals(count[i][0]) && !trans[j].from.equals(count[i][0])) {
                        count[i][1] = Integer.toString(Integer.parseInt(count[i][1]) + 1);
                        k++;
                    }
                }
            }
            //if any state has not any links with others(no in, no out), yield an error
            for (int i = 0; i < states.length; i++) {
                if (Integer.parseInt(count[i][1]) == 0) {
                    isE2 = true;
                }
            }
        }

    }

    /**
     * Error 3: A transition a is not represented in the alphabet
     * the method yields an error any of transitions is not represented in the alphabet of transitions
     * String out[] - an array of transitions not in the set of alphabet
     */
    public String[] checkError3(String alpha[], Transaction trans[]) {
        String out[] = new String[alpha.length + trans.length];
        Arrays.fill(out, stub);
        String tr[] = new String[trans.length];
        for (int i = 0; i < trans.length; i++) {
            if (trans[i].title != null) {
                tr[i] = trans[i].title;
            }
        }

        boolean isInSet[] = new boolean[alpha.length + trans.length];
        Arrays.fill(isInSet, false);
        for (int i = 0; i < tr.length; i++) {
            boolean t = false;
            // checking all the transitions if they are in the alphabet, memorizing indexes of absenting
            for (int j = 0; j < alpha.length; j++) {
                if (tr[i].equals(alpha[j])) {
                    t = true;
                }
            }
            isInSet[i] = t;
            t = false;
        }
        int k = 0;
        // if the transition is absent, put it to the output array
        for (int i = 0; i < isInSet.length; i++) {
            if (!isInSet[i] && i < tr.length) {
                out[k] = tr[i];
                k++;
            }
        }
        if (!out[0].equals(stub)) {
            isE3 = true;
        }

        return out;
    }

    /**
     * Error 4: Initial state is not defined
     * the method yields an error if initial state is not defined
     */
    public void checkError4(String initSt) {
        if (initSt.isEmpty()) {
            isE4 = true;
        }
    }

    /**
     * Report: FSA is complete/incomplete
     */
    public void makeReport(String states[], Transaction trans[], String alpha[]) {
        String count[][] = new String[states.length][50];
        for (int i = 0; i < states.length; i++) {
            count[i][0] = states[i];
            count[i][1] = "0"; //number of outTransitions from the state
        }
        //memorizing the transitions out of each state
        for (int i = 0; i < states.length; i++) {
            int k = 2;
            for (int j = 0; j < trans.length; j++) {
                if (trans[j].from.equals(count[i][0])) {
                    count[i][k] = trans[j].title;
                    count[i][1] = Integer.toString(Integer.parseInt(count[i][1]) + 1);
                    k++;
                }
            }
            count[i][k] = stub;
        }

        int det[] = new int[states.length];
        Arrays.fill(det, 0);
        //for every state check whether all transitions from alphabet is present, counting the number of last
        for (int i = 0; i < states.length; i++) {
            int j = 2;
            while (!count[i][j].equals(stub)) {
                for (int k = 0; k < alpha.length; k++) {
                    if (count[i][j].equals(alpha[k])) {
                        det[i]++;
                    }
                }
                j++;
            }
        }
        // if for any state there're less different transitions than in the alphabet, yealds "FSA is incomplete" report, "FSA is complete" otherwise
        for (int i = 0; i < det.length; i++) {
            if (det[i] != alpha.length) {
                isComp = false;
            }
        }
    }

    /**
     * Warning 1: Accepting state is not defined
     */
    public void checkWarning1(String state[], String finalSt[]) {
        if (finalSt[0].isEmpty()) {
            isW1 = true;
        }
        boolean isInSet[] = new boolean[finalSt.length];
        Arrays.fill(isInSet, false);
        for (int i = 0; i < isInSet.length; i++) {
            for (int j = 0; j < state.length; j++) {
                if (finalSt[i].equals(state[j])) {
                    isInSet[i] = true;
                }
            }
        }
        for (int i = 0; i < isInSet.length; i++) {
            if (!isInSet[i]) {
                isW1 = true;
            }
        }
    }

    /**
     * Warning 2: Some states are not reachable from initial state
     * the method yields a warning  if there is at least 1 state that is not reachable( apart if we consider the fsa as ndirected graph)
     */
    public void checkWarning2(String states[], Transaction trans[]) {
        if (states.length != 1) {
            String count[][] = new String[states.length][2];
            for (int i = 0; i < states.length; i++) {
                count[i][0] = states[i];
                count[i][1] = "0";
            }
            for (int i = 0; i < states.length; i++) {
                int k = 1;
                for (int j = 0; j < trans.length; j++) {
                    if (trans[j].to.equals(count[i][0]) && !trans[j].from.equals(count[i][0])) {
                        count[i][1] = Integer.toString(Integer.parseInt(count[i][1]) + 1);
                        k++;
                    }
                }
            }
            for (int i = 0; i < states.length; i++) {
                if (Integer.parseInt(count[i][1]) == 0) {
                    isW2 = true;
                }
            }
        }
    }

    /**
     * Warning 3: FSA is nondeterministic
     * the method yields a warning if there is at least 1 state that has the same transition to 2 or more states
     */
    public void checkWarning3(String states[], String alpha[], Transaction trans[]) {

        String count[][] = new String[states.length][50];
        for (int i = 0; i < states.length; i++) {
            count[i][0] = states[i];
        }

        //for each state memorizing the numb of ou transitions
        for (int i = 0; i < states.length; i++) {
            int k = 1;
            for (int j = 0; j < trans.length; j++) {
                if (trans[j].from.equals(count[i][0])) {
                    count[i][k] = trans[j].title;
                    k++;
                }
            }
            count[i][k] = stub;
        }
        for (int i = 0; i < states.length; i++) {
            int j = 1;
            int repetitions[] = new int[alpha.length];
            Arrays.fill(repetitions, 0);

            //counting the numb of each transition from the alphabet for the state
            while (!count[i][j].equals(stub)) {
                for (int p = 0; p < alpha.length; p++) {
                    if (count[i][j].equals(alpha[p])) {
                        repetitions[p]++;
                    }
                }
                j++;
                //checks throw the list of the transitions for the state if they repeat
                for (int k = 0; k < repetitions.length; k++) {
                    if (repetitions[k] > 1) {
                        isW3 = true;
                    }
                }
            }

        }


    }
}
