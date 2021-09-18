///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.apache.commons:commons-math3:3.6.1
//DEPS info.picocli:picocli:4.5.0

// JBang deps and picocli
import java.io.FileWriter;
import java.io.IOException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static java.lang.System.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.math3.primes.*;
import java.math.BigInteger;

//TODO upgrade and deal with BigInt for more fun
@Command(name = "TruncatablePrimes", mixinStandardHelpOptions = true,
        version = "TruncatablePrimes 0.1",
        description = "see https://youtu.be/azL5ehbw_24")
public class TruncatablePrimes implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-s", "--max-size"},
            description = "The max length of target primes",
            required = true)
    private int size;

    public static HashSet<Integer> seed() {
        HashSet<Integer> out = new HashSet<>();
        // initialize
        for (int i = 1; i < 10; i++) {
            if (Primes.isPrime(i)) {
                out.add(i);
                System.out.println("Added <" + i + ">");
            }
        }
        return out;
    }

    public static HashSet<Integer> nextGen(int aTrunc) {
        HashSet<Integer> out = new HashSet<Integer>();
        //ArrayList<Integer> tmp = new ArrayList<Integer>();
        for (int i = 1; i < 10; i++) {
            String lString = i + "" + aTrunc;
            //System.out.println("Candidate string : <" + lString + ">");
            if (Primes.isPrime(Integer.parseInt(lString))) {
                out.add(Integer.parseInt(lString));
                System.out.println("Added prime : <" + Integer.parseInt(lString) + ">");
            }
        }
        return out;
    }

    public static HashSet<Integer> nextGen(HashSet<Integer> in) {
        HashSet<Integer> out = new HashSet<Integer>();

        if (in.size() == 0) {
            return seed();
        }
        Iterator<Integer> iter = in.iterator();
        while (iter.hasNext()) {
            Integer i = iter.next();
            out.addAll(TruncatablePrimes.nextGen(i));
        }
        System.out.println("Output set size : <" + out.size() + ">");
        return out;
    }

    public static HashSet<Integer> generate(int targetLength) {

        HashSet<Integer> lSeed = new HashSet<Integer>();
        for (int i = 1; i < targetLength; i++) {
            System.out.println("===============================================");
            System.out.println("Compute " + i + " th. generation.");
            lSeed = TruncatablePrimes.nextGen(lSeed);
        }
        return lSeed;
    }

    private void printlnAnsi(String msg) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string(msg));
    }

    public static void generateGraphml(int targetLength) throws IOException {
        // open file in append mode
        FileWriter gml = new FileWriter("LeftTruncatablePrimes.graphml");
        // put header
        gml.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        gml.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
                + "     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n"
                + "<graph id=\"G\" edgedefault=\"directed\">");
        gml.flush();

        // for each size, create/append nodes and keep them in hashSet
        HashSet<Integer> lSeed = new HashSet<Integer>();
        for (int i = 1; i < targetLength; i++) {
            System.out.println("===============================================");
            System.out.println("Compute " + i + " th. generation.");
            lSeed = TruncatablePrimes.nextGen(lSeed);
            Iterator<Integer> iter = lSeed.iterator();
            while(iter.hasNext()){
               // gml.write("" + iter.next() + "\n");
               gml.write("<node id=\"" + iter.next() + "\"/>\n");
            }
            gml.flush();
        }
        // now lSeed has the biggest set
        
        // for each element of lSeed, create relationships
        Iterator<Integer> iter = lSeed.iterator();
            while(iter.hasNext()){
                // deal with a dumb string
                String s = iter.next() + "";
                //graphMl.write("" + iter.next() + "\n");
                for(int i = 0 ; i < s.length() -1 ; i++ ){
                    // print the found relationship
                    System.out.println(s.substring(i, s.length()));
                    gml.write("<edge source=\"" + s.substring(i, s.length()) + "\" target=\"" + s.substring(i+1, s.length()) + "\"/>\n");
                    gml.flush();
                }
            }
            gml.write("</graph>\n"
                + "</graphml>");
        gml.flush();
        gml.close();
            
        
    }
    
    public static void main(String... args) {
        int exitCode = new CommandLine(new TruncatablePrimes()).execute(args);
        System.exit(exitCode);
    }
    
    

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.println("=================== Left Truncated Primes ================");
        System.out.println("About to generate sets :\n");

        System.out.println("Input max size : " + size);
        if(size <= 10){
            printlnAnsi("@|green -> About o compute|@");
        }
        else{
            printlnAnsi("@|red -> Exceeds int capacity. Won't run. Retry with less or equals than 10|@");
            return 1;
        }
        //TruncatablePrimes.generate(size);
        TruncatablePrimes.generateGraphml(size);
        return 0;
    }
}
