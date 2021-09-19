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

@Command(name = "BigTruncatablePrimes", mixinStandardHelpOptions = true,
        version = "BigTruncatablePrimes 0.1",
        description = "see https://youtu.be/azL5ehbw_24")
public class BigTruncatablePrimes implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-s", "--max-size"},
            description = "The max length of target primes",
            required = true)
    private int size;

    public static HashSet<BigInteger> seed() {
        HashSet<BigInteger> out = new HashSet<>();
        // initialize
        for (int i = 1; i < 10; i++) {
            if (Primes.isPrime(i)) {
                //out.add(i);
                out.add(BigInteger.valueOf(i));
                System.out.println("Added <" + i + ">");
            }
        }
        return out;
    }

    public static HashSet<BigInteger> nextGen(BigInteger aTrunc) {
        HashSet<BigInteger> out = new HashSet<BigInteger>();

        for (int i = 1; i < 10; i++) {
            String lString = i + "" + aTrunc;
            //System.out.println("Candidate string : <" + lString + ">");
            BigInteger l = new BigInteger(lString);
            if (l.isProbablePrime(1)) {
                out.add(l);
                System.out.println("Added prime : <" + l + ">");
            }
        }
        return out;
    }

    public static HashSet<BigInteger> nextGen(HashSet<BigInteger> in) {
        HashSet<BigInteger> out = new HashSet<BigInteger>();

        if (in.size() == 0) {
            return seed();
        }
        Iterator<BigInteger> iter = in.iterator();
        while (iter.hasNext()) {
            BigInteger i = iter.next();
            out.addAll(BigTruncatablePrimes.nextGen(i));
        }
        System.out.println("Output set size : <" + out.size() + ">");
        return out;
    }

    public static HashSet<BigInteger> generate(int targetLength) {

        HashSet<BigInteger> lSeed = new HashSet<BigInteger>();
        for (int i = 1; i < targetLength; i++) {
            System.out.println("===============================================");
            System.out.println("Compute " + i + " th. generation.");
            lSeed = BigTruncatablePrimes.nextGen(lSeed);
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
        HashSet<BigInteger> lSeed = new HashSet<BigInteger>();
        for (int i = 1; i < targetLength; i++) {
            System.out.println("===============================================");
            System.out.println("Compute " + i + " th. generation.");
            lSeed = BigTruncatablePrimes.nextGen(lSeed);
            if(lSeed.size() == 0){
                System.out.println("Houray !!! We got them all !.");
                break;
            }
            //if (lSeed.size() > 0) {
                
                Iterator<BigInteger> iter = lSeed.iterator();
                while (iter.hasNext()) {
                    // gml.write("" + iter.next() + "\n");
                    gml.write("<node id=\"" + iter.next() + "\"/>\n");
                }
                gml.flush();
                

                // for each element of lSeed, create relationships
                Iterator<BigInteger> iter2 = lSeed.iterator();
                while (iter2.hasNext()) {
                    // deal with a dumb string
                    String s = iter2.next() + "";
                    //graphMl.write("" + iter.next() + "\n");
                    for (int j = 0; j < s.length() - 1; j++) {
                        // print the found relationship
                        System.out.println(s.substring(j, s.length()));
                        gml.write("<edge source=\"" + s.substring(j, s.length()) + "\" target=\"" + s.substring(j + 1, s.length()) + "\"/>\n");
                        gml.flush();
						if (j == (s.length() - 2)){
						System.out.println(s.substring(j+1, s.length()));
						}
                    }
                }
            

        }
        // now lSeed has the biggest set

        gml.write("</graph>\n"
                + "</graphml>");
        gml.flush();
        gml.close();

    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new BigTruncatablePrimes()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.println("=================== Left Truncated Primes ================");
        System.out.println("About to generate sets :\n");

        System.out.println("Input max size : " + size);
        /*if(size <= 10){
            printlnAnsi("@|green -> About o compute|@");
        }
        else{
            printlnAnsi("@|red -> Exceeds int capacity. Won't run. Retry with less or equals than 10|@");
            return 1;
        }
         */
        //TruncatablePrimes.generate(size);
        BigTruncatablePrimes.generateGraphml(size);
        return 0;
    }
}
