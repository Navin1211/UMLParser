import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * 
 * Parser code to parse Java files and convert it to plantuml grammar for generation of UML class diagram.
 * @author navinkumarpatil
 *
 */
public class UMLParser {

	private static HashSet<String> files = new HashSet<String>();// change name
	private static ArrayList<InputGrammar> inputGrammarList = new ArrayList<InputGrammar>();
	public static HashSet<String> interfacesInProject = new HashSet<String>();
	InputGrammar inputGrammar;
	public static final int LENGTH = 2;
	static Map<String, ArrayList<String>> ballSocket = new HashMap<String, ArrayList<String>>();



	/**
	 * Main method
	 * @param args
	 */
	public static void main(String args[]) {
		UMLParser umlParser = getInstance();
		if (args.length < LENGTH) {
			System.out.println("Arguments Invalid.");
			return;
		}
		umlParser.readInputData(args[0], args[1]);
	}


	void readInputData(String folder, String outputFile) {
		CompilationUnit cu = null;
		UMLParser umlParser = getInstance();
		GenerateGrammar generateGrammar = new GenerateGrammar();
		File directory = new File(folder);
		File[] javaFileNames = directory.listFiles();
		for (File javaFile : javaFileNames) {
			if (javaFile.getName().endsWith("java")) {
				files.add(javaFile.getName().split("\\.")[0]);
			}
		}
		for (File javaFile : javaFileNames) {
			try {
				if (javaFile.getName().endsWith("java")) {
					files.add(javaFile.getName().split("\\.")[0]);
					FileInputStream in = new FileInputStream(javaFile.getAbsolutePath());
					umlParser.parseJavaCode(cu, in);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		generateGrammar.grammarGeneration(inputGrammarList, outputFile);

	}

	void parseJavaCode(CompilationUnit cu, FileInputStream in) {

		String javaClassName;
		inputGrammar = new InputGrammar();

		try {
			cu = JavaParser.parse(in);

			// For Classes Start
			List<TypeDeclaration> typeDeclaration = cu.getTypes();
			javaClassName = typeDeclaration.get(0).getName();
			for (TypeDeclaration typedeclare : typeDeclaration) {
				if (typedeclare instanceof ClassOrInterfaceDeclaration) {
					inputGrammar.setJavaClass(typedeclare.getName());
					if (!typedeclare.toString().contains(" interface ")) {
						inputGrammar.setClassOrInterfaceType(true);
					}

					List<ClassOrInterfaceType> extendType = ((ClassOrInterfaceDeclaration) typedeclare).getExtends();
					List<ClassOrInterfaceType> implementType = ((ClassOrInterfaceDeclaration) typedeclare)
							.getImplements();
					if (extendType != null) {
						for (ClassOrInterfaceType extendClass : extendType) {
							inputGrammar.setJavaParentClass(extendClass.getName());
						}
					}
					if (implementType != null) {
						HashSet<String> interfacesList = new HashSet<String>();
						for (ClassOrInterfaceType interfaceName : implementType) {

							interfacesList.add(interfaceName.getName());
							interfacesInProject.add(interfaceName.getName());
							if(ballSocket.isEmpty())

							{
								
								ballSocket.put(interfaceName.getName(), new ArrayList<String>());

								ballSocket.get(interfaceName.getName()).add(typedeclare.getName());

							}else if(ballSocket.get(interfaceName.getName())== null) {

								ballSocket.put(interfaceName.getName(), new ArrayList<String>());

								ballSocket.get(interfaceName.getName()).add(typedeclare.getName());

							}else {

								ballSocket.get(interfaceName.getName()).add(typedeclare.getName());

							}



						}
						inputGrammar.setJavaInterface(interfacesList);
					}

				}
			}
			// Classes End

			// Method Visitor Start
			new MethodVisitor().visit(cu, inputGrammar);
			//Method Visitor End

			// Fields Start

			Map<String, String> fieldsMap = new HashMap<String, String>();

			for (TypeDeclaration typedeclare : typeDeclaration) {

				List<BodyDeclaration> javaMembers = typedeclare.getMembers();
				ArrayList<String> javaVariables = new ArrayList<String>();
				List<String> javaClassObjects = new ArrayList<String>();
				for (BodyDeclaration fields : javaMembers) {
					if (fields instanceof FieldDeclaration) {
						FieldDeclaration javaType = (FieldDeclaration) fields;
						List<VariableDeclarator> javaFields = javaType.getVariables();
						int modifiers = javaType.getModifiers();
						if (javaType.getType() instanceof ReferenceType) {
							ReferenceType referenceType = (ReferenceType) javaType.getType();
							int arrayCount = referenceType.getArrayCount();
							if (referenceType.getType() instanceof PrimitiveType) {

								String parameter = javaFields.toString().substring(1, javaFields.toString().length());
								String getMethod = "get" + parameter.substring(0, 1).toUpperCase()
										+ parameter.substring(1, parameter.length() - 1);
								String setMethod = "set" + parameter.substring(0, 1).toUpperCase()
										+ parameter.substring(1, parameter.length() - 1);

								if (inputGrammar.getJavaMethodOnly().contains(getMethod)
										|| inputGrammar.getJavaMethodOnly().contains(setMethod)) {
									modifiers = 1;
									inputGrammar.getJavaMethod()
											.remove(inputGrammar.getJavaMethodOnly().indexOf(setMethod));
									inputGrammar.getJavaMethodOnly().remove(setMethod);
									inputGrammar.getJavaMethod()
											.remove(inputGrammar.getJavaMethodOnly().indexOf(getMethod));
									inputGrammar.getJavaMethodOnly().remove(getMethod);
								}

								if (!inputGrammar.isClassOrInterfaceType()) {
									modifiers = 1;
								}

								switch (modifiers) {
								case 2:
									javaVariables.add(
											"-" + javaFields.toString().substring(1, javaFields.toString().length() - 1)
													+ " : " + javaType.getType());
									break;
								case 1:
									javaVariables.add(
											"+" + javaFields.toString().substring(1, javaFields.toString().length() - 1)
													+ " : " + javaType.getType());
									break;
								case 1025:
									javaVariables.add(
											"+" + javaFields.toString().substring(1, javaFields.toString().length() - 1)
													+ " :" + javaType.getType());
									break;
								}

							}
							if (referenceType.getType() instanceof ClassOrInterfaceType) {
								ClassOrInterfaceType javaClassOrInterfaceType = (ClassOrInterfaceType) referenceType
										.getType();
								if (javaClassOrInterfaceType.getTypeArgs() != null) {
									String packageName = javaClassOrInterfaceType.getTypeArgs().getClass().getPackage()
											.getName();

									if (javaClassOrInterfaceType.getTypeArgs().getClass().getPackage().getName()
											.equals("java.util")) {
										if (files.contains(javaClassOrInterfaceType.getTypeArgs().get(0).toString())) {
											fieldsMap.put(inputGrammar.getJavaClass() + ":"
													+ javaClassOrInterfaceType.getTypeArgs().get(0), "* ");

										} else {
											fieldsMap.put(inputGrammar.getJavaClass() + ":"
													+ javaClassOrInterfaceType.getName(), "1");
										}
									}

								} else if (files.contains(javaClassOrInterfaceType.getName())) {
									fieldsMap.put(
											inputGrammar.getJavaClass() + ":" + javaClassOrInterfaceType.getName(),
											"1");
								} else {

									String parameter = javaFields.toString().substring(1,
											javaFields.toString().length());
									String getMethod = "get" + parameter.substring(0, 1).toUpperCase()
											+ parameter.substring(1, parameter.length() - 1);
									String setMethod = "set" + parameter.substring(0, 1).toUpperCase()
											+ parameter.substring(1, parameter.length() - 1);

									if (inputGrammar.getJavaMethodOnly().contains(getMethod)
											|| inputGrammar.getJavaMethodOnly().contains(setMethod)) {
										modifiers = 1;
										inputGrammar.getJavaMethod()
												.remove(inputGrammar.getJavaMethodOnly().indexOf(setMethod));
										inputGrammar.getJavaMethodOnly().remove(setMethod);
										inputGrammar.getJavaMethod()
												.remove(inputGrammar.getJavaMethodOnly().indexOf(getMethod));
										inputGrammar.getJavaMethodOnly().remove(getMethod);
									}

									if (!inputGrammar.isClassOrInterfaceType()) {
										modifiers = 1;
									}
									switch (modifiers) {
									case 2:
										javaVariables.add("-"
												+ javaFields.toString().substring(1, javaFields.toString().length() - 1)
												+ " : " + javaType.getType());
										break;
									case 1:
										javaVariables.add("+"
												+ javaFields.toString().substring(1, javaFields.toString().length() - 1)
												+ " : " + javaType.getType());
										break;
									case 1025:
										javaVariables.add("+"
												+ javaFields.toString().substring(1, javaFields.toString().length() - 1)
												+ " : " + javaType.getType());
										break;
									}
								}

							}

						} else if (javaType.getType() instanceof PrimitiveType) {
							if (javaType.getType().getClass().equals(PrimitiveType.class)) {

								String parameter = javaFields.toString().substring(1, javaFields.toString().length());
								String getMethod = "get" + parameter.substring(0, 1).toUpperCase()
										+ parameter.substring(1, parameter.length() - 1);
								String setMethod = "set" + parameter.substring(0, 1).toUpperCase()
										+ parameter.substring(1, parameter.length() - 1);

								if (inputGrammar.getJavaMethodOnly().contains(getMethod)
										|| inputGrammar.getJavaMethodOnly().contains(setMethod)) {
									modifiers = 1;
									inputGrammar.getJavaMethod()
											.remove(inputGrammar.getJavaMethodOnly().indexOf(setMethod));
									inputGrammar.getJavaMethodOnly().remove(setMethod);
									inputGrammar.getJavaMethod()
											.remove(inputGrammar.getJavaMethodOnly().indexOf(getMethod));
									inputGrammar.getJavaMethodOnly().remove(getMethod);
								}

								if (!inputGrammar.isClassOrInterfaceType()) {
									modifiers = 1;
								}
								switch (modifiers) {
								case 2:
									javaVariables.add(
											"-" + javaFields.toString().substring(1, javaFields.toString().length() - 1)
													+ ":" + javaType.getType());
									break;
								case 1:
									javaVariables.add(
											"+" + javaFields.toString().substring(1, javaFields.toString().length() - 1)
													+ ":" + javaType.getType());
									break;
								}
							}

						}
						String classType = javaType.getType().getClass().getName().toString();
						System.out.println("Class Name : " + classType);

					}

				}
				inputGrammar.setJavaField(javaVariables);
				inputGrammar.setJavaClassObject(javaClassObjects);
				inputGrammar.setJavaFieldObject(fieldsMap);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		inputGrammarList.add(inputGrammar);

	}

	private static class MethodVisitor extends VoidVisitorAdapter {

		UMLParser umlParser = getInstance();
		ArrayList<String> javaMethodList = new ArrayList<String>();
		ArrayList<String> methodNames = new ArrayList<String>();
		HashSet<String> javaDependeciesList = new HashSet<String>();

		@Override
		public void visit(ConstructorDeclaration constructor, Object arg) {
			umlParser.inputGrammar = (InputGrammar) arg;
			String arguments = "";
			if (constructor.getParameters() != null) {
				List<Parameter> constructorParameterName = constructor.getParameters();
				for (Parameter constructorParameter : constructorParameterName) {
					String constructorParameters[] = constructorParameter.toString().split(" ");
					String parameter = constructorParameters[0];
					if (files.contains(parameter)) {
						arguments += constructorParameters[1] + " : " + parameter + ",";
						javaDependeciesList.add(parameter);
					} else {
						arguments = arguments + constructorParameters[1] + " : " + parameter + ", ";
					}
				}
				if (!arguments.isEmpty()) {
					arguments = arguments.substring(0, arguments.length() - 1);
				}
				int modifiers = constructor.getModifiers();
				switch (modifiers) {
				case 1:
					javaMethodList.add("+ " + constructor.getName() + "(" + arguments + ")");
					break;
				case 2:
					javaMethodList.add("- " + constructor.getName() + "()");
					break;
				}

			} else {

				int modifiers = constructor.getModifiers();
				switch (modifiers) {
				case 1:
					javaMethodList.add("+ " + constructor.getName() + "()");
					break;
				case 1025:
					javaMethodList.add("+ " + constructor.getName() + "()");
					break;
				}
			}
			if (!javaMethodList.isEmpty()) {
				umlParser.inputGrammar.setJavaMethod(javaMethodList);
			}
			if (!javaDependeciesList.isEmpty()) {
				umlParser.inputGrammar.setJavaDependencies(javaDependeciesList);
			}
		}

		@Override
		public void visit(MethodDeclaration method, Object arg) {
			umlParser.inputGrammar = (InputGrammar) arg;
			ArrayList<String> parameterNames = new ArrayList<String>();
			methodNames.add(method.getName());
			umlParser.inputGrammar.setJavaMethodOnly(methodNames);

			if (method.getBody() != null) {
				List<Statement> MethodStatement = method.getBody().getStmts();
				if (MethodStatement != null) {
					for (Statement body : MethodStatement) {
						parameterNames.add(body.toString().split(" ")[0]);
					}
					for (String param : parameterNames) {
						if (umlParser.files.contains(param)) {
							javaDependeciesList.add(param);
						}
					}
				}
			}
			if (method.getParameters() != null) {
				String arguments = "";
				List<Parameter> parameterName = method.getParameters();

				for (Parameter methodParams : parameterName) {
					String methodName[] = methodParams.toString().split(" ");
					String parameter = methodName[0];

					if (files.contains(parameter)) {
						arguments += methodName[1] + " : " + parameter + ",";
						javaDependeciesList.add(parameter);
					} else {
						arguments += methodName[1] + " : " + parameter + ", ";
					}
				}

				if (!arguments.isEmpty()) {
					arguments = arguments.substring(0, arguments.length() - 1);
				}


				 int modifiers = method.getModifiers();
				 switch (modifiers) {
				 case 0:
				 javaMethodList.add("+" + method.getName()+ "("+ arguments +") : " + method.getType());
				 break;
				 case 1:
				 javaMethodList.add("+" + method.getName()+ "("+ arguments +") : " + method.getType());
				 break;
				 case 2:
				 break;
				 case 9:
				 javaMethodList.add("+" + method.getName()+ "("+ arguments +") : " + method.getType());
				 break;
				 case 1025:
				 javaMethodList.add("+" + method.getName()+ "("+ arguments +") : " + method.getType());
				 break;
				 }

			} else {


				 int modifiers = method.getModifiers();
				 switch (modifiers) {
				 case 1:
				 javaMethodList.add("+" + method.getName()+ "() : " + method.getType());
				 break;
				 case 2:
				 break;
				 case 0:
				 javaMethodList.add("+" + method.getName()+ "() : " +method.getType());
				 break;
				 case 9:
				 javaMethodList.add("+" + method.getName()+ "() : " + method.getType());
				 break;
				 case 1025:
				 javaMethodList.add("+" + method.getName()+ "() : " +method.getType());
				 break;
				 }

			}

			if (!javaMethodList.isEmpty()) {
				umlParser.inputGrammar.setJavaMethod(javaMethodList);
			}
			if (!javaDependeciesList.isEmpty()) {
				umlParser.inputGrammar.setJavaDependencies(javaDependeciesList);
			}
		}

	}
	
	private static UMLParser getInstance() {
		UMLParser umlParser = new UMLParser();
		return umlParser;
	}
}
