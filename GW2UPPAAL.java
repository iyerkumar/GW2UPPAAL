package xmlCreation;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class GW2UPPAAL {
	
	public  String xmlFilePath = "C:\\Users\\Acer\\Desktop\\GWModels\\PetClinic.xml";
	
	//method to add Element
	public static Element addElement(Document doc,String nodeName ,Element parent){
		
		Element node = doc.createElement(nodeName);
		
		
		if(parent != null){
			parent.appendChild(node);
		}
		
		return node;
	}
	
	
	//method to add location tag
	public static Element addLocation(Document doc,Element template ,String id, String x , String y, String nodeName ){
		
		Element location = doc.createElement("location");
		location.appendChild(doc.createTextNode(nodeName));
		
		template.appendChild(location);
		
		Attr attrId = doc.createAttribute("id");
		attrId.setValue(id);
		
		Attr attrX = doc.createAttribute("x");
		attrX.setValue(x);
		
		Attr attrY = doc.createAttribute("y");
		attrY.setValue(y);
		
		location.setAttributeNode(attrId);
		location.setAttributeNode(attrX);
		location.setAttributeNode(attrY);
		
		
		return location;
		
	}
	
	//add Name tag
	public static void addName(Document doc,Element name, String x, String y, String nodeName){
		
		
		
		Attr attrX = doc.createAttribute("x");
		attrX.setValue(x);
		
		Attr attrY = doc.createAttribute("y");
		attrY.setValue(y);
		
		name.setAttributeNode(attrX);
		name.setAttributeNode(attrY);
		name.appendChild(doc.createTextNode(nodeName));
		
		
		
	}
	
	public static void addRef(Document doc, Element init, String id){
		Attr ref = doc.createAttribute("ref");
		ref.setValue(id);
		
		init.setAttributeNode(ref);
	}
	
	public static Element addEdge(Document doc, Element template, String sorceVertexId, String targetVertexId, String labelX, String labelY){
		
		Element transition = doc.createElement("transition");
		
		
		return transition;
	}
	
	
	private static void addLabel(Document doc, Element label, String x, String y, String kindValue,String labelValue) {
		// TODO Auto-generated method stub
		
		Attr kind = doc.createAttribute("kind");
		kind.setValue(kindValue);
		
		Attr attrX = doc.createAttribute("x");
		attrX.setValue(x);
		
		Attr attrY = doc.createAttribute("y");
		attrY.setValue(y);
		
		label.setAttributeNode(kind);
		label.setAttributeNode(attrX);
		label.setAttributeNode(attrY);
		
		Text labelName = doc.createTextNode(labelValue);
		label.appendChild(labelName);
		
	}
	
	public static void addTextNodeValue(Document doc, Element elementName, String value){
		
		Text nodeTextVal = doc.createTextNode(value);
		elementName.appendChild(nodeTextVal);
	}
	
	

	public static void main(String[] args) throws Exception{
		
		long startTime = System.currentTimeMillis();
		System.out.println("Start time: " + startTime);
		
		//JSONParser
		String inputFilePath = args[2];
		String inpurtFileName = args[3];
		
		//   F://Thesis//jsonFile//PetClinic.json
		FileReader fr = new FileReader(inputFilePath+"//"+inpurtFileName);
		
		JSONParser jsonParser = new JSONParser();
		JSONObject obj = (JSONObject) jsonParser.parse(fr);
		
		//get model's list
		JSONArray modelList = (JSONArray)obj.get("models");
		
		
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = df.newDocumentBuilder();
		
		Document doc = db.newDocument();
		
		
		//root element
		Element root = addElement(doc,"nta",null);
		doc.appendChild(root);
		
		
		//add global declaration tag
				Element declaration = addElement(doc,"declaration",root);
				addTextNodeValue(doc, declaration, "//global variables go here \n ");
		
		
		
		
		//HashMap to store the vertex and its x and y coorindates
		HashMap<String, String> vertexMap = new HashMap<String,String>();
		
		//HashSet to store all edges to add there declaration in global variable declaration
		HashSet<String> edgeSet = new HashSet<String>();
		HashSet<String> vertexName = new HashSet<String>();
		
		HashMap<String,String> sharedVertexMap = new HashMap<String,String>();
		HashMap<String,String> vertexToshareStateMap = new HashMap<String,String>();
		
		//Hashmap to store the edge id and its corresponding source and destination ids.
		HashMap<String,ArrayList<String>> edgeMap = new HashMap<String,ArrayList<String>>();
		
		//As we have only 1 init
		/*int countVal = 0;*/
		
		Element template = addElement(doc,"template",root);
		
		
		//add Name tag 
		Element name = addElement(doc,"name",template);
		
		
		
		
		
		//add attribute x and y in name tag
		addName(doc,name,"5","5","CombinedModel");
		
		//local declaration
		Element declarationLocal = addElement(doc,"declaration",template);
		addTextNodeValue(doc, declarationLocal, "//local variables go here");
		JSONObject nameJson = null;
		String nameFromJson = null;
		String startElementId = null;
				for(Object o:modelList){
					//add template
					
					
					
					//extract vertex Info from Json
					JSONObject vertexArrayObj = (JSONObject)o;
					JSONArray vertexArray = (JSONArray)vertexArrayObj.get("vertices");
				//	System.out.println(vertexArray.size());
					
					
					//This counter is to add it to the vertexname if a duplicate exists
					int counter = 1;
					for(Object v:vertexArray){
					
					//This will be a loop for adding locations as many as vertices
					//extract name of vertex, x coordinate, y coordinate
					//loop for vertex creation:
						
						//extractin id of json vertex
						JSONObject idObj = (JSONObject)v;
						String id = (String)idObj.get("id");
						//System.out.println(id);
						JSONObject sharedState = (JSONObject)v;
						String sharedStateName = (String)sharedState.get("sharedState");
						//System.out.println(id+" "+sharedStateName);
						if(sharedStateName != null && sharedStateName.trim().length() > 0){
							//System.out.println("here");
							vertexToshareStateMap.put(id, sharedStateName);
						}
						if(sharedStateName == null ||sharedStateName.trim().length()==0|| (sharedStateName != null && !(sharedVertexMap.containsKey(sharedStateName)))){
							//extracting name of vertex from json
							nameJson = (JSONObject)v;
							nameFromJson = (String)nameJson.get("name");
							
							char[] nameArray = nameFromJson.toCharArray();
							
							for(int i = 0 ; i < nameArray.length; i++){
								
								if(!((nameArray[i] >= 'a' && nameArray[i] <= 'z') || (nameArray[i] >= 'A' && nameArray[i] <= 'Z'))){
									nameArray[i] = '_';
								} 
							}
							
							nameFromJson = new String(nameArray);
							
							if(vertexName.contains(nameFromJson)){
								nameFromJson = nameFromJson+Integer.toString(counter++);
							}
							vertexName.add(nameFromJson);
							
							//extracting x and y coordinates from json
							JSONObject propertiesObj = (JSONObject)v;
							
							JSONObject properties = (JSONObject)propertiesObj.get("properties");
							
							Random rand = new Random();
							
							String xVal = (String)properties.get("x").toString() + Integer.toString(rand.nextInt(500));
							if(Math.abs((int)Double.parseDouble(xVal)) >= 1000)
								xVal = Integer.toString((int)Double.parseDouble(xVal)/1000);
							else
								xVal = Integer.toString((int)Double.parseDouble(xVal));
							String yVal = (String)properties.get("y").toString() + Integer.toString(rand.nextInt(500));
							
							if(Math.abs((int)Double.parseDouble(yVal)) >= 1000)
								yVal = Integer.toString((int)Double.parseDouble(yVal)/1000);
							else
								yVal = Integer.toString((int)Double.parseDouble(yVal));
							
							
							
							//put values in map
							
								vertexMap.put(id, xVal+","+yVal);
							
								Element location = addLocation(doc, template, id, xVal, yVal, nameFromJson);
											
								//Name tags x and y value will be extracted from GW
								name = addElement(doc,"name",template);
								addName(doc,name,xVal,yVal,nameFromJson);
								location.appendChild(name);
								
								if(sharedStateName != null){
									sharedVertexMap.put(sharedStateName, id);
								}
						}


						
						
						
						
						
						//Extracting initial vertex from json
						JSONObject startElementIdObj = null;
						startElementIdObj = (JSONObject)o;
						
						
							startElementId	= (String)startElementIdObj.get("startElementId");
							//System.out.println("startElementId: " +startElementId);
							
								
							//System.out.println(startElementId);
							
					
								
							
						}
						
					
					}
					
					
					Element init = null;
					init = addElement(doc,"init",template);
					
						
						
					for(Object o:modelList){
					
						//extracting edjes from json
						JSONObject edgeArrayObj = (JSONObject)o;
						JSONArray edgeArray = (JSONArray)edgeArrayObj.get("edges");
						
						ArrayList<String> edgeVertexList = null;
						//loop for edge creation
						for(Object v:edgeArray){
								JSONObject idObj = (JSONObject)v;
								String id = (String)idObj.get("id");
								
							
								//extracting source and targetvertex ids from json
								JSONObject sourceVertexIdObj = (JSONObject)v;
								String sourceVertexId = (String)sourceVertexIdObj.get("sourceVertexId");
								if(vertexToshareStateMap.containsKey(sourceVertexId)){
									sourceVertexId = sharedVertexMap.get(vertexToshareStateMap.get(sourceVertexId));
								}
								
								JSONObject targetVertexIdObj = (JSONObject)v;
								String targetVertexId = (String)targetVertexIdObj.get("targetVertexId");
								if(vertexToshareStateMap.containsKey(targetVertexId)){
									targetVertexId = sharedVertexMap.get(vertexToshareStateMap.get(targetVertexId));
								}
								
								edgeVertexList = new ArrayList<String>();
								
								edgeVertexList.add(sourceVertexId);
								edgeVertexList.add(targetVertexId);
								
								edgeMap.put(id,edgeVertexList);
								edgeVertexList = null;
							//	System.out.println(edgeMap.get(id));
						
								
								Element transition = addElement(doc,"transition",template);
								Element source = addElement(doc,"source",transition);
								addRef(doc, source, sourceVertexId);
								
								Element target = addElement(doc,"target",transition);
								addRef(doc, target, targetVertexId);
								
								
								/*//extract name of edge from json
								nameJson = (JSONObject)v;
								nameFromJson = (String)nameJson.get("name");
								
								char[] nameArray = nameFromJson.toCharArray();
								
								for(int i = 0 ; i < nameArray.length; i++){
									
									if(!(nameArray[i] >= 'a' && nameArray[i] <= 'z') || !(nameArray[i] >= 'A' && nameArray[i] <= 'Z')){
										nameArray[i] = '_';
									} 
								}
								
								nameFromJson = new String(nameArray);*/
								
								String sourceVertexCoordinates[] = vertexMap.get(sourceVertexId).split(",");
								int sourceX = (int)Double.parseDouble(sourceVertexCoordinates[0]);
								int sourceY = (int)Double.parseDouble(sourceVertexCoordinates[1]);
								
								String targetVertexCoordinates[] = vertexMap.get(targetVertexId).split(",");
								int targetX = (int)Double.parseDouble(targetVertexCoordinates[0]);
								int targetY = (int)Double.parseDouble(targetVertexCoordinates[1]);
								
								
								String labelX = Integer.valueOf(sourceX + (targetX-sourceX)/2).toString();
								String labelY = Integer.valueOf(sourceY + (targetY-sourceY)/2).toString();
								Element label = addElement(doc,"label",transition);
								addLabel(doc,label,labelX,labelY,"synchronisation","run!");
								
								
								//method to add guard condition
								JSONObject guard = (JSONObject)v;
								
								String guardCondition = (String)guard.get("guard");
							//	System.out.println("guardCondition: "+guardCondition);
								if(guardCondition != null && guardCondition.length()>0 && guardCondition != "" && guardCondition.charAt(guardCondition.length()-1)==';')
									guardCondition = guardCondition.substring(0,guardCondition.length()-1);
							//	System.out.println(guardCondition);
								if(guardCondition != null){
									label = addElement(doc,"label",transition);
									addLabel(doc,label,labelX,labelY,"guard",guardCondition);
									//System.out.println("addedGuard");
								}
								
								
								//method to add actions
								JSONObject actions = (JSONObject)v;
								JSONArray action = (JSONArray)actions.get("actions");
								if(action != null){
									for(int i = 0 ; i< action.size();i++){
										String actionVal  = (String)action.get(i);
										if(actionVal.length()>1 && actionVal.charAt(actionVal.length()-1) == ';')
											actionVal = actionVal.substring(0,actionVal.length()-1);
										label = addElement(doc,"label",transition);
										addLabel(doc,label,labelX,labelY,"assignment",actionVal);
										//System.out.println(j);
									}
								
								}
								
								
								
								
								
								
								edgeSet.add(nameFromJson);
							
							
						//also method of nail (future)
						
					
					
					}
						
					if(edgeMap.containsKey(startElementId) && edgeMap.get(startElementId).get(0)!=null )
						startElementId = edgeMap.get(startElementId).get(0);
				
					addRef(doc,init,startElementId);
					
					
					//add global Variable declaration
					JSONObject actionsObject = (JSONObject)o;
					JSONArray globalActionArray = (JSONArray)actionsObject.get("actions");
					
					if(globalActionArray != null){
						for(int i = 0 ; i< globalActionArray.size();i++){
							String actionVal  = ((String)globalActionArray.get(i)).trim();
							
							if(actionVal.charAt(actionVal.length()-1) == ';'){
								actionVal = actionVal.substring(0,actionVal.length()-1);
							}
						//	System.out.println(actionVal);
							if(actionVal.charAt(actionVal.length()-1) == 'e')
								addTextNodeValue(doc, declaration, "\nbool "+actionVal+";");
							else
								addTextNodeValue(doc, declaration, "\nint "+actionVal+";");
							
							
							
						}
					
					}
						
				}
	
					
					//method to add dummy model
					template = addElement(doc,"template",root);
					
					
					
					//add Name tag 
					name = addElement(doc,"name",template);
					
					
					
					
					
					//add attribute x and y in name tag
					addName(doc,name,"5","5","DummyModel");
					
					//local declaration
					declarationLocal = addElement(doc,"declaration",template);
					addTextNodeValue(doc, declarationLocal, "//local variables go here");
					
					
					Element location = addLocation(doc, template, "dummyId", "100", "100", "initialVertex");
					
					name = addElement(doc,"name",template);
					addName(doc,name,"110","110","initialVertex");
					location.appendChild(name);
					
					init = addElement(doc,"init",template);
					addRef(doc,init,"dummyId");
					
					
					Element transition = addElement(doc,"transition",template);
					
					Element source = addElement(doc,"source",transition);
					addRef(doc, source, "dummyId");
											
					Element target = addElement(doc,"target",transition);
					addRef(doc, target, "dummyId");
					
					
					
					Element label = addElement(doc,"label",transition);
					addLabel(doc,label,"100","90","synchronisation","run?");
					
					
		
				
				
		//system variables declaration
			Element system = addElement(doc, "system", root);
			addTextNodeValue(doc, system, "//template instantiations go here");
			
			addTextNodeValue(doc, system, "\n c1 = CombinedModel(); \n t1 = DummyModel(); \n system c1,t1;");
			
		
			
		String chanDeclaration = "\nchan run;";
		/*int count = 0;
		int size = edgeSet.size();
		
		for(String channel: edgeSet){
			count++;
			System.out.println(count);
			if(count == size)
				chanDeclaration += channel+";";
			else
				chanDeclaration += channel+",";
		}*/
		// add channel creation to global variable
		addTextNodeValue(doc, declaration, chanDeclaration);
		
		
		//method to add queries for verifier
		
		Element queries = addElement(doc, "queries", root);
		Iterator itr = vertexName.iterator();
		
		while(itr.hasNext()){
			Element query = addElement(doc, "query", queries);
			Element formula = addElement(doc,"formula",query);
			addTextNodeValue(doc, formula, "E<> c1."+itr.next());
		}
		
		Element query = addElement(doc, "query", queries);
		Element formula = addElement(doc,"formula",query);
		addTextNodeValue(doc, formula, "A[] not deadlock");
		
		//create xml file 
		//transform dom object to xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		
		//code to add doctype
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		DOMImplementation domImpl = doc.getImplementation();
		DocumentType doctype = domImpl.createDocumentType("doctype",
			    "-//Uppaal Team//DTD Flat System 1.1//EN",
			    "http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
		
		//code to create xml
		DOMSource domSource = new DOMSource(doc);
		String outputFilePath = args[0];
		String outputFileName = args[1];
		StreamResult streamResult = new StreamResult(new File(outputFilePath+"//"+outputFileName));
		transformer.transform(domSource, streamResult);
		System.out.println("No. of vertices: " +vertexName.size());
		System.out.println("No. of edges: " +edgeMap.size());
		long endTime = System.currentTimeMillis();
		System.out.println("End time: " + endTime);
		
		Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"verifyta "+outputFileName+"\"");
		//Process P = Runtime.getRuntime().exec(new String[] {"cmd", "/K", "Start"});
		//Process P = Runtime.getRuntime().exec("verifyta "+"PetClinic.xml");
		System.out.println("UPPAAL Model generated");
		System.out.println("Total time taken: "+ (endTime-startTime));
	}


	


	
}
