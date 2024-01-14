package com.testng.runner;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class Runner {

    private static final Logger log = LogManager.getLogger(Runner.class.getName());

    public static void main(String[] args){

        String currentWorkingDirectory = System.getProperty("user.dir");

        String suite = System.getProperty("suite_path");

        String defaultSuite = currentWorkingDirectory +"/" + "test-suite.xlsx" ;

        String suitePath = suite != null? suite:defaultSuite;

        log.info("Referring to the file: "+suitePath);

        Map<String, Set<String>> classMap = read_suite(suitePath);

        generateTestNGxml(classMap);


    }

    public static Map<String, Set<String>> read_suite(String suitePath){


        String testType = System.getenv("testType");
        String sheet = System.getenv("sheet");

        testType = testType!=null?testType:"smoke";
        sheet = sheet!=null?sheet: "suite";

        Fillo fillo = new Fillo();
        try {
            Connection connection = fillo.getConnection(suitePath);

            String query = "SELECT * FROM "+sheet+" WHERE " +testType+ " = True";

            log.info("Query used to generate the test suite: "+query);

            Recordset recordset = connection.executeQuery(query);

            Map<String, Set<String>> classMap = new HashMap<>();

            while(recordset.next()){
                String className = recordset.getField("classpath");
                String methodName = recordset.getField("method");
                Set<String> methods;

                if (classMap.containsKey(className)){
                    methods = classMap.get(className);

                    if(!methodName.isEmpty()) {
                        methods.add(methodName);
                        classMap.put(className, methods);
                    }
                }
                else {
                    methods = methodName.isEmpty() ? Collections.emptySet() : new HashSet<>(Collections.singletonList(methodName));
                    classMap.put(className, methods);
                }
            }
            return classMap;
        } catch (FilloException e) {
            throw new RuntimeException(e);
        }

    }

    public static void generateTestNGxml(Map<String,Set<String>> classMap){
        XmlSuite suite = new XmlSuite();
        suite.setName("GeneratedSuite");

        // Create an instance of XmlTest and set its name
        XmlTest test = new XmlTest(suite);
        test.setName("GeneratedTest");

        // Create a list of XmlClasses
        List<XmlClass> classes = new ArrayList<>();

        for(String key : classMap.keySet()) {
            XmlClass testClass = new XmlClass(key);
            classes.add(testClass);

            if(!classMap.get(key).isEmpty()) {
                // Add methods to include
                List<XmlInclude> methodsToRun = new ArrayList<>();
                for (String method:classMap.get(key))
                    methodsToRun.add(new XmlInclude(method));

                // Set included methods in XmlClass
                testClass.setIncludedMethods(methodsToRun);
            }
        }
        // Assign classes to the test
        test.setXmlClasses(classes);

        // Create TestNG object
        TestNG testng = new TestNG();
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);

        // Optionally, run the suite
        // testng.run();

        // Print the suite for verification or write it to a file
        log.info(suite.toXml());

        testng.run();

    }

}
