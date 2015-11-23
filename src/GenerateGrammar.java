import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.plantuml.SourceStringReader;

/**
 * 
 * This class generates the grammar required for the PlantUML and converts the
 * java files to class diagram.
 * 
 * @author navinkumarpatil
 *
 */
public class GenerateGrammar {

	public void grammarGeneration(List<InputGrammar> inputGrammarList, String outputFile) {

		StringBuilder generatedGrammar = new StringBuilder();
		generatedGrammar.append("@startuml" + "\n");
		generatedGrammar.append("skinparam classAttributeIconSize 0 ");

		for (InputGrammar inputGrammar : inputGrammarList) {

			if (inputGrammar.getJavaParentClass() != null) {
				if (inputGrammar.getJavaDependencies().contains(inputGrammar.getJavaParentClass())
						&& (inputGrammar.getJavaDependencies().equals(inputGrammar.getJavaParentClass()))) {

				} else {
					generatedGrammar.append(inputGrammar.getJavaParentClass() + " <|-- " + inputGrammar.getJavaClass());
				}
			}
			generatedGrammar.append("\n");

			// if (!inputGrammar.getJavaInterface().isEmpty()) {
			// for (String interfaceName : inputGrammar.getJavaInterface()) {
			// generatedGrammar.append(interfaceName + " <|.. " +
			// inputGrammar.getJavaClass());
			// generatedGrammar.append("\n");
			// }
			// }

			if (!inputGrammar.getJavaDependencies().isEmpty()) {
				for (String dependency : inputGrammar.getJavaDependencies()) {
					if (UMLParser.interfacesInProject.contains(dependency)) {
						// generatedGrammar.append(inputGrammar.getJavaClass()+
						// " ..> \"uses\" "+ dependency+"\n");
						if (UMLParser.ballSocket.containsKey(dependency)) {

							generatedGrammar.append(inputGrammar.getJavaClass() + " -(0- "
									+ UMLParser.ballSocket.get(dependency).get(0) + ":" + dependency + "\n");

							UMLParser.ballSocket.remove(dependency);

						}
					}
				}
			}

			if (inputGrammar.isClassOrInterfaceType()) {
				generatedGrammar.append("class " + inputGrammar.getJavaClass() + " {\n");
			} 
//			else {
//				generatedGrammar.append("interface " + inputGrammar.getJavaClass() + " {\n");
//			}

			if (inputGrammar.isClassOrInterfaceType() && inputGrammar.getJavaMethod() != null) {
				for (String method : inputGrammar.getJavaMethod()) {
					generatedGrammar.append(method);
					generatedGrammar.append("\n");
				}
			}

			if (inputGrammar.isClassOrInterfaceType() && !inputGrammar.getJavaField().isEmpty()) {
				for (String field : inputGrammar.getJavaField()) {
					generatedGrammar.append(field);
					generatedGrammar.append("\n");
				}

			}
			if(inputGrammar.isClassOrInterfaceType())
			generatedGrammar.append(" }\n");

			if (!inputGrammar.getJavaClassObject().isEmpty()) {
				for (String classObj : inputGrammar.getJavaClassObject()) {
					generatedGrammar.append(classObj);
					generatedGrammar.append("\n");
				}

			}

		}
		
		if(!UMLParser.ballSocket.isEmpty()) {
			int count = 0;

			for (String interfaceName : UMLParser.ballSocket.keySet()) {

			generatedGrammar.append(interfaceName + " ()- " + UMLParser.ballSocket.get(interfaceName).get(0) + "\n");



			}
			
		}

		Map<String, String> fieldsMap = new ConcurrentHashMap<String, String>();

		for (InputGrammar inputGrammar : inputGrammarList) {
			if (!inputGrammar.getJavaFieldObject().isEmpty()) {
				fieldsMap.putAll(inputGrammar.getJavaFieldObject());
			}

		}

		Set<String> keySet = fieldsMap.keySet();
		String reverseKey = null;
		for (String key : keySet) {
			if (key.equals(reverseKey)) {
				continue;
			}
			String[] keyString = key.split("\\:");
			reverseKey = keyString[1] + ":" + keyString[0];
			if (UMLParser.ballSocket.containsKey(keyString[1])) {
				if (keySet.contains(reverseKey)) {
					generatedGrammar.append(keyString[0] + " \"" + fieldsMap.get(reverseKey) + "\"-- \""
							+ fieldsMap.get(key) + "\" " + keyString[1] + "\n");
					System.out.println("grammarstring........" + keyString[0] + " \"" + fieldsMap.get(reverseKey)
							+ "\"-- \"" + fieldsMap.get(key) + "\" " + keyString[1] + "\n");
					fieldsMap.remove(key);
					fieldsMap.remove(reverseKey);
					keySet.remove(key);
					keySet.remove(reverseKey);
				} else {
					generatedGrammar.append(keyString[0] + " -- " + keyString[1] + "\n");
				}

			}
		}

		generatedGrammar.append("@enduml");
		System.out.println("grammar generated: " + generatedGrammar);
		generateImage(generatedGrammar.toString(), outputFile);

	}

	void generateImage(String source, String outputFile) {
		SourceStringReader reader = new SourceStringReader(source);
		try {
			FileOutputStream file = new FileOutputStream(outputFile);
			reader.generateImage(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
