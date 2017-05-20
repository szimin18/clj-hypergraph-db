package orientdb.crud;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * Created by Puszek_SE on 2015-04-12.
 */

public class MainClass {

    CRUD container;

    public static void main(String[] args){
        MainClass testInstance = new MainClass();
        testInstance.container = new CRUD();
        try{
            testInstance.container.database.commit();
            OrientVertexType base = testInstance.container.addVertexType("Base");
            base.createProperty("name",OType.STRING);

            OrientVertexType child = testInstance.container.addExtendingClass("Child", "Base");
            child.createProperty("val",OType.INTEGER);

            OrientVertexType toy = testInstance.container.addExtendingClass("Toy", "Child");
            toy.createProperty("kind",OType.STRING);

            Vertex vToy = testInstance.container.database.addVertex("class:Toy");
            testInstance.container.database.begin();
            vToy.setProperty("name","kid's Toy");
            vToy.setProperty("kind","nice");
            vToy.setProperty("val",123);

            Vertex vBase = testInstance.container.database.addVertex("class:Base");
            vBase.setProperty("name","Base vertex instance");

            String label="1";
            testInstance.container.database.addEdge(null,vBase,vToy,label);


            System.out.println(vToy.toString());
            for(String key : vToy.getPropertyKeys()){
                System.out.println(key+": "+ vToy.getProperty(key));
            }
            System.out.println();


            testInstance.container.database.commit();

            System.out.println("VERTEXES FOR 'BASE' CLASS WITH POLYMORPHISM");
            for(Vertex v : testInstance.container.getVertexesForClass("Base")){
                System.out.println(v.toString());
                for(String key : v.getPropertyKeys()){
                    System.out.println(key+": "+ v.getProperty(key));
                }
                System.out.println();
            }

            System.out.println("VERTEXES FOR 'BASE' CLASS WITHOUT POLYMORPHISM");
            for(Vertex v : testInstance.container.getVertexesForSpecificClass("Base")){
                System.out.println(v.toString());
                for(String key : v.getPropertyKeys()){
                    System.out.println(key+": "+ v.getProperty(key));
                }
                System.out.println();
            }

            System.out.println("VERTEXES FOR 'Toy' CLASS WITH POLYMORPHISM");
            for(Vertex v : testInstance.container.getVertexesForClass("Toy")){
                System.out.println(v.toString());
                for(String key : v.getPropertyKeys()){
                    System.out.println(key+": "+ v.getProperty(key));
                }
                System.out.println();
            }

            System.out.println("VERTEXES FOR 'Toy' CLASS WITHOUT POLYMORPHISM");
            for(Vertex v : testInstance.container.getVertexesForSpecificClass("Toy")) {
                System.out.println(v.toString());
                for (String key : v.getPropertyKeys()) {
                    System.out.println(key + ": " + v.getProperty(key));
                }
                System.out.println();
            }
            //testInstance.container.documentTx - PODPIĘCIE DOKUMENTOWE DO GRAFOWEJ BAZY DANYCH


            //testInstance.importSchema(new TestScenario());
        }
        finally{
            testInstance.container.destroy();
        }
    }

    /*private void importSchema(TestScenario scenario){
        for(String className : scenario.testClasses.keySet()){

            OrientVertexType modifiedClass = container.addVertexType(className);
            Map<String,TestScenario.Property> propertyTypes = scenario.testClasses.get(className);
            for(String propName : propertyTypes.keySet()){
                TestScenario.Property property = propertyTypes.get(propName);
                if
                modifiedClass.createProperty(propName,propertyTypes.get(propName));
            }
        }
    }*/
}
