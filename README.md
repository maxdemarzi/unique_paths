# Unique Paths
Stored Procedure to find unique nodes.

This project requires Neo4j 3.2.x

Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/matcher-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/unique-1.0-SNAPSHOT.jar neo4j-enterprise-3.2.3/plugins/.


Edit your Neo4j/conf/neo4j.conf file by adding this line:

    dbms.security.procedures.unrestricted=com.maxdemarzi.*    

Restart your Neo4j Server.

Create the Schema by running this stored procedure:

        CALL com.maxdemarzi.schema.generate
    
Let's create some test data. First the root of our traversal:

        CREATE (u:PRODUCT {id:"1-Root"})
    
Next we will create a few layers of non PRODUCT labeled nodes:    
    
        WITH ["PlayStation","Lipitor","Corolla","Star Wars","iPad","Mario Bros. Franchise","Michael Jackson’s Thriller","Harry Potter",
        "iPhone","Rubik’s Cube"] AS products
        MATCH (product:PRODUCT) WHERE product.id STARTS WITH "1-"
        WITH range(1,2) as children, product, products
        FOREACH (id in children | CREATE (product)-[:NEEDS]->(:Item {id:"2-" + 
        products[id% size(products)]+id(product)}) );
        
        WITH ["PlayStation","Lipitor","Corolla","Star Wars","iPad","Mario Bros. Franchise","Michael Jackson’s Thriller","Harry Potter",
            "iPhone","Rubik’s Cube"] AS products
        MATCH (item:Item) WHERE item.id STARTS WITH "2-"
        WITH range(1,2) as children, item, products
        FOREACH (id in children | CREATE (item)-[:NEEDS]->(:Item {id:"3-" + 
        products[id% size(products)]+id(item)}) );
        
        WITH ["PlayStation","Lipitor","Corolla","Star Wars","iPad","Mario Bros. Franchise","Michael Jackson’s Thriller","Harry Potter",
            "iPhone","Rubik’s Cube"] AS products
        MATCH (item:Item) WHERE item.id STARTS WITH "3-"
        WITH range(1,2) as children, item, products
        FOREACH (id in children | CREATE (item)-[:NEEDS]->(:Item {id:"4-" + 
        products[id% size(products)]+id(item)}) );
    
Finally we will create the PRODUCT labeled nodes:
    
        WITH ["PlayStation","Lipitor","Corolla","Star Wars","iPad","Mario Bros. Franchise","Michael Jackson’s Thriller","Harry Potter",
            "iPhone","Rubik’s Cube"] AS products
        MATCH (item:Item) WHERE item.id STARTS WITH "4-"
        WITH range(1,2) as children, item, products
        FOREACH (id in children | CREATE (item)-[:NEEDS]->(:PRODUCT {id:"5-" + 
        products[id% size(products)]+id(item)}) );
        
Now call the stored procedure:
        
        CALL com.maxdemarzi.unique('1-Root') yield node return node.id
        
Try the alternate Cypher query:
         
        MATCH (p:PRODUCT {id: "1-Root"})-[:NEEDS*..10000]->(d:PRODUCT)         
        RETURN distinct d.id;