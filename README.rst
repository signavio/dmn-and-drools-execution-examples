|Header|

======
DMN & Drools (DRL) Execution Example via Signavio
======

`Signavio Website <https://signavio.com>`_
• `Docs <https://docs.signavio.com/>`_

|Build|


**Disclaimer:**

  This project shows how the artifacts exported from Signavio Process Manager can be used to execute the decision logic.
  The execution engine we use in our examples is the one provided by RedHat (https://www.drools.org/).
  For any engine specific questions, consult the documentation provided there.



.. contents:: **Contents**
  :backlinks: none

Setup
======
This project is self-contained and doesn't need any external configuration.

You can import the project into your preferred IDE after checking out the repository via:

``git clone git@github.com:signavio/dmn-and-drools-execution-examples.git``

To allow the execution of the provided example, you first need to install libs/bdm-test-suite-api-1.1.jar and
libs/dmn-formulae-java8-1.1.jar into your local Maven repository. You can do this by executing the following commands:

``mvn install:install-file -Dfile=libs/bdm-test-suite-api-1.1.jar -DgroupId=com.signavio -DartifactId=bdm-testsuite-api -Dversion=1.1 -Dpackaging=jar``

``mvn install:install-file -Dfile=libs/dmn-formulae-java8-1.1.jar -DgroupId=com.signavio -DartifactId=dmn-formulae -Dversion=1.1 -Dpackaging=jar``

You can also just run

``mvn initialize``

Or any other default maven build commands

Project Structure
=================
Entry Point
------------

``com.signavio.examples.SignavioExamples``

This class lets you execute all prepared examples in one go.

Resources
----------

All resources we use, mainly the decision logic in either DRL or DMN format and
the JSON files exported from the Signavio Test Lab, are stored in the resources folder under com.signavio.examples.

To make the examples easier to understand, we use the following decision diagram in nearly all cases.

|DRG|
|DL|

Examples
--------

All the examples we provide are located in their respective packages under
**com.signavio.examples.drl** and **com.signavio.examples.dmn**, respectively.

For the execution of DRL files, the following examples are available:

* **SimpleDrlExample** shows the most basic form of setting input values and retrieving all the outputs

* **OwnTypesDrlExample** makes use of classes external to the DRL file

* **DrlWithTestCasesExample** additionally executes a set of test cases exported from the Signavio Test Lab

* **DynamicSandboxDrlExample** configures the knowledge base dynamically instead of using the kmodules.xml files for that (this example is **not** using the DMN diagram mentioned above)

* **DrlSandbox** lets you play around with different DRL files (this example is **NOT** using the DMN diagram mentioned above)

Both sandbox examples are using com/signavio/examples/drl/sandbox/Sandbox.drl as their source.

For the execution of DMN files, the following examples are available

* **SimpleDmnExample** shows the most basic form of setting input values and retrieving outputs

* **DmnWithTestCasesExample** additionally executes a set of test cases exported from the Signavio Test Lab

* **DmnSandbox** lets you play around with different DMN files (this example is **NOT** using the DMN diagram mentioned above)

Signavio Artifacts
==================
DMN
-----
When exporting DMN diagrams from Signavio Process Manager as DMN files, you receive a single artifact,
the DMN file itself.
You can directly use this file as the source of your knowledge base.

We recommend to check the exported file, because Signavio Process Manager is uniquely naming all the important variables like input data. The information that something in the diagram was renamed because of unsupported
characters or duplicate names is needed to ensure that the
correct value is used when setting input values or retrieving specific outputs.

For example, if you define two different input data and name them both **my value**, both of them are renamed,
to **myValue** and **myValue2**.

DRL
-----
When exporting DMN diagrams from Signavio Process Manager as Drools (DRL) files, you receive an archive containing the
exported rules (as a DRL file), a manifest file, and a formulae JAR file.

Those artifacts serve different purposes during the execution. The DRL file is used as the source of your knowledge base and describes the decision logic itself. The formulae.jar file must be made available to the execution engine
because it contains function definitions for the drools execution. Using the manifest file is optional but recommended.

General Workflow
=================
The workflow for executing a DMN file or a DRL file is similar.

Retrieving a knowledge base
----------------------------
First, you have to retrieve a knowledge base. The examples show two different ways of achieving this.

configuring the knowledge base via kmodules.xml
-----------------------------------------------
You can define the knowledge base via the kmodules.xml file located in resources/META-INF/

.. code-block:: xml

  <kbase name="KnowledgeBaseName" packages="any.package.name1, any.package.name2">
    <ksession name="SessionName"/>
  </kbase>

The knowledge base automatically parses all files located in the defined packages. This in turn also means that all
files are treated as a single source of decision logic.

configuring the knowledge base via API
--------------------------------------
If kmodules.xml is not sufficient, because the knowledge base has to be configured dynamically, you can also
create the knowledge base via an API.



.. code-block:: java

  private KieSession newKieSession(Reader drlReader) {
      InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

      KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      knowledgeBuilder.add(ResourceFactory.newReaderResource(drlReader), ResourceType.DRL);
      knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());

      return knowledgeBase.newKieSession();
  }

This allows for a more dynamic way of configuring the knowledge base and supports use cases where the DRL and DMN
files are not present or available when triggering the application.

Setting inputs
==============
After retrieving a session from a configured knowledge base, you have to set the input values for the execution.
Depending on whether you want to execute a DRL or a DMN file, the way of setting those differs slightly.

DMN
-----
When setting input values for a DMN file execution, named key-value pairs are passed to a DMNContext that can be retrieved from the session.

.. code-block:: java

  protected DMNContext createDmnContext() {
      DMNContext dmnContext = getDmnRuntime().newContext();

      // setting values for inputs
      dmnContext.set("name", "John Doe");
      dmnContext.set("age", 35);

      return dmnContext;
  }

DRL
-----

When setting input values for a DRL file execution, the input objects must be constructed and inserted into the session.
This is done by retrieving the type of the object (so-called fact type), instantiating an object with that fact type, and setting
all the fields.

.. code-block:: java

  try {
      // creating input object defined in the DRL file
      FactType inputType = getInputFactType();
      Object input = inputType.newInstance();

      // setting all given values to there respective fields
      inputType.set(input, "name", "John Doe");
      inputType.set(input, "age", 35);

      return input;
  } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
  }

This object is then inserted into the session with

.. code-block:: java

  ksession.insert(input);

To figure out which fact types are available and which fields belong to them, you can consult the manifest file
available inside the exported archive.

Triggering the execution
========================
In both cases, triggering the actual execution is simple.

DMN
------
For a DMN file, you trigger the execution with

.. code-block:: java

  getDmnRuntime().evaluateAll(model, dmnContext);

providing the model you want to evaluate (available in the session) and the previously created context (input values).

DRL
------
For a DRL file, you trigger the execution with

.. code-block:: java

  ksession.fireAllRules();

because all inputs are already set in the session.

Retrieving the outputs
=====================
After the execution has finished, the next step is to retrieve the produced output values.

DMN
-----
The evaluation of the decision logic mentioned above already returns the result.

.. code-block:: java

  DMNResult result = getDmnRuntime().evaluateAll(model, dmnContext);

This result can then be used to retrieve the actual output values.
Important to note is that this result also contains the intermediate results of all decisions.

DRL
-----
For a DRL file, the execution does not automatically return the result. To get access to the result, you can
retrieve all the available objects from the session.

.. code-block:: java

  ksession.getObjects();

The session also provides some methods to filter for specific
types of objects.

Test Suite Testcases
=====================
Signavio Process Manager can export test cases defined in the Signavio Test Suite.
The exported JSON representation of the test case looks like the one provided in
resources/com/signavio/examples/dmn/simple/Simple-TestLab.json.
Those files contain several input definitions that can be used to figure out which inputs to set

.. code-block:: json

  "inputParameterDefinitions": [
    {
      "id": "cb7e33e39ee644da9a4bb48b1cc74e65/sid-D7DF30A5-56A7-4043-86FC-EF3595C49355",
      "shapeId": "sid-D7DF30A5-56A7-4043-86FC-EF3595C49355",
      "diagramId": "cb7e33e39ee644da9a4bb48b1cc74e65",
      "modelName": "Simple",
      "requirementName": "Customer Years"
    },
    {
      "id": "cb7e33e39ee644da9a4bb48b1cc74e65/sid-CE8F3937-3DA2-41AB-AF9C-B7F301C6D8E4",
      "shapeId": "sid-CE8F3937-3DA2-41AB-AF9C-B7F301C6D8E4",
      "diagramId": "cb7e33e39ee644da9a4bb48b1cc74e65",
      "modelName": "Simple",
      "requirementName": "Customer Level"
    }
  ]

They also contain some output definitions in the same format. The IDs provided in the file can be used to find the
corresponding input in the DMN and DRL files.
For example

.. code-block:: xml

  <inputData name="customerLevel" sigExt:shapeId="sid-CE8F3937-3DA2-41AB-AF9C-B7F301C6D8E4" sigExt:diagramId="cb7e33e39ee644da9a4bb48b1cc74e65">


Additionally, those files contain several test cases with their respective input values and the expected outputs.
The order of those values is the same as in the input definitions, this means the first defined input value corresponds
to the first defined input definition.

.. code-block:: json

  "testCases": [
    {
      "inputValues": [
        {
          "type": "number",
          "value": 0
        },
        {
          "type": "enumeration",
          "value": "0",
          "name": "None"
        }
      ],
      "expectedValues": [
        {
          "type": "number",
          "value": 0
        }
      ]
    }
  ]

In our examples, we use the ``bdm-test-suite-api`` library to handle the JSON files.

Sandbox Workflow
===============
The sandbox is available to get a quick feedback loop in case you want to try out specific DRL or DMN files.

Export
------
To get it running, first export the desired DMN model as a Drools (DRL) or DMN file in
Signavio Process Manager.

Pasting the file content
------------------------
Next, copy the content of the exported file into the already available
``com/signavio/examples/drl/sandbox/Sandbox.drl`` file (for DRL files) or
``com/signavio/examples/dmn/sandbox/Sandbox.dmn`` file (for DMN files).
To execute a DRL file, the exported artifacts package definition does not match the one needed in
this example project, you have to manually adjust the package in the DRL file to

.. code-block:: java

  package com.signavio.examples.drl.sandbox

Executing
----------
Open either com.signavio.examples.drl.DrlSandbox or com.signavio.examples.drl.DmnSandbox and adjust the inputs to the
ones needed in your example.
Afterwards, you can trigger the SignavioExamples.java to run all examples (including the sandbox).

.. |Build| image:: https://github.com/signavio/dmn-and-drools-execution-examples/workflows/Java%20CI%20with%20Maven/badge.svg
   :target: https://github.com/signavio/dmn-and-drools-execution-examples/actions?query=workflow%3A%22Java+CI+with+Maven%22
   :alt: build_pipleine_with_maven

.. |DRG| image:: https://github.com/signavio/dmn-and-drools-execution-examples/raw/master/img/Simple.svg
   :alt: simple_decision_relation_graph

.. |DL| image:: https://github.com/signavio/dmn-and-drools-execution-examples/raw/master/img/DecisionLogic.PNG
   :target: https://github.com/signavio/dmn-and-drools-execution-examples/actions?query=workflow%3A%22Java+CI+with+Maven%22
   :alt: decision_logic

.. |Header| image:: https://www.signavio.com/wp-content/uploads/2019/09/product-pages-illustrations-suite-1-1.png
   :alt: header

You can also execute from maven.
``mvn package exec:java``

You can change kie version used
``mvn package exec:java -Dkie.version=7.43.1.Final``

You can change the executable class in case you want to run a different example
``mvn package exec:java -Dartifact.mainClass=com.signavio.examples.dmn.AdHocDmnExample``
or
``mvn package exec:java -Dartifact.mainClass=com.signavio.examples.dmn.AdHocDrlExample``

You can build the project and then execute without maven
``mvn package``

``java -jar target/dmn-and-drools-execution-examples-1.0.0.jar``

``java -jar target/dmn-and-drools-execution-examples-1.0.0.jar com.signavio.examples.dmn.AdHocDmnExample ``
``java -jar target/dmn-and-drools-execution-examples-1.0.0.jar com.signavio.examples.drl.AdHocDrlExample ``


