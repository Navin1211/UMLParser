import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * InputGrammar 
 * @author navinkumarpatil
 *
 */
public class InputGrammar {

	
	/**
	 * Java Class
	 */
	String javaClass;
	
	boolean classOrInterfaceType =false;
	
	/**
	 * Java Interface
	 */
	HashSet<String> javaInterface = new HashSet<String>();
	
	/**
	 * Java Method
	 */
	ArrayList<String> javaMethod = new ArrayList<String>();
	
	/**
	 * Java Methods Only
	 */
	ArrayList<String> javaMethodOnly = new ArrayList<String>();
	
	/**
	 * Java Parent Class
	 */
	String javaParentClass;
		
	/**
	 * Java Field
	 */
	ArrayList<String> javaField;
	
	/**
	 * Java Class Object
	 */
	List<String> javaClassObject = new ArrayList<String>();
	
	/**
	 * Java Field Object
	 */
	Map<String,String> javaFieldObject;
	
	/**
	 * Java Dependencies
	 */
	HashSet<String> javaDependencies = new HashSet<String>();

	/**
	 * Get the Java Class
	 * @return String
	 */
	public String getJavaClass() {
		return javaClass;
	}

	/**
	 * Set the Java Class
	 * @param javaClass
	 */
	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	/** 
	 * Get the Java Method
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getJavaMethod() {
		return javaMethod;
	}

	/**
	 * Set the Java Method
	 * @param javaMethod
	 */
	public void setJavaMethod(ArrayList<String> javaMethod) {
		this.javaMethod = javaMethod;
	}

	/**
	 * Get the Java Parent Class
	 * @return String
	 */
	public String getJavaParentClass() {
		return javaParentClass;
	}

	/**
	 * Set the Java PArent Class
	 * @param javaParentClass
	 */
	public void setJavaParentClass(String javaParentClass) {
		this.javaParentClass = javaParentClass;
	}

	/**
	 * Get the Java Interface
	 * @return HashSet<String>
	 */
	public HashSet<String> getJavaInterface() {
		return javaInterface;
	}

	/**
	 * Set the Java Interface
	 * @param javaInterface
	 */
	public void setJavaInterface(HashSet<String> javaInterface) {
		this.javaInterface = javaInterface;
	}

	/**
	 * Get the Java Class or Interface Type 
	 * @return boolean
	 */
	public boolean isClassOrInterfaceType() {
		return classOrInterfaceType;
	}

	/**
	 * Set the Java Class or Interface Type
	 * @param classOrInterfaceType
	 */
	public void setClassOrInterfaceType(boolean classOrInterfaceType) {
		this.classOrInterfaceType = classOrInterfaceType;
	}

	/**
	 * Get the Java Field
	 * @return ArrayList<String> 
	 */
	public ArrayList<String> getJavaField() {
		return javaField;
	}

	/**
	 * Set the Java Field
	 * @param javaField
	 */
	public void setJavaField(ArrayList<String> javaField) {
		this.javaField = javaField;
	}

	/**
	 * Get the Java Dependencies
	 * @return HashSet<String> 
	 */
	public HashSet<String> getJavaDependencies() {
		return javaDependencies;
	}

	/**
	 * Set the Java Dependencies
	 * @param javaDependencies
	 */
	public void setJavaDependencies(HashSet<String> javaDependencies) {
		this.javaDependencies = javaDependencies;
	}

	/**
	 * Get the Java Class Object
	 * @return List<String>
	 */
	public List<String> getJavaClassObject() {
		return javaClassObject;
	}

	/**
	 * Set the Java Class Object
	 * @param javaClassObject
	 */
	public void setJavaClassObject(List<String> javaClassObject) {
		this.javaClassObject = javaClassObject;
	}

	/**
	 * Get the Java Field Object
	 * @return Map<String, String>
	 */
	public Map<String, String> getJavaFieldObject() {
		return javaFieldObject;
	}

	/**
	 * Set the Java Field Object
	 * @param javaFieldObject
	 */
	public void setJavaFieldObject(Map<String, String> javaFieldObject) {
		this.javaFieldObject = javaFieldObject;
	}

	/**
	 * Get the Java Method Only
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getJavaMethodOnly() {
		return javaMethodOnly;
	}

	/**
	 * Set the Java Method Only
	 * @param javaMethodOnly
	 */
	public void setJavaMethodOnly(ArrayList<String> javaMethodOnly) {
		this.javaMethodOnly = javaMethodOnly;
	}


	
}
