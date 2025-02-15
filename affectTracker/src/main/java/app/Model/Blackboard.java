package app.Model;

import app.Data.Circle;
import app.Data.ProcessedDataObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.*;


/**
 * The {@code Blackboard} class serves as the central hub for managing data across different components
 * of the system. It holds data queues for eye-tracking and emotion information, a list of circles for the display,
 * and settings for server information and display behavior.
 * <p>
 * This class follows the singleton design pattern, ensuring that only one instance of {@code Blackboard}
 * exists during the application's lifecycle. It provides synchronized access to the data being exchanged
 * between components, and manages the state of data retrieval.
 *
 * @author Andrew Estrada
 * @author Sean Sponsler
 * @version 1.0
 */
public class Blackboard
	//Todo: add extends PropertyChangeSupport
{
	private String eyeTrackingSocket_Host = "localhost";  // default for testing
	private int eyeTrackingSocket_Port = 6001;  // default for testing
	private final BlockingQueue<String> eyeTrackingQueue;
	private String emotionSocket_Host = "localhost"; // default for testing
	private int emotionSocket_Port = 6000; // default for testing
	private final BlockingQueue<String> emotionQueue;
	private final Queue<ProcessedDataObject> processedDataQueue;
	public static final String PROPERTY_NAME_PROCESSED_DATA = "processed data";
	
	// Todo: remove this variable
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	public static final String PROPERTY_NAME_VIEW_DATA = "view data";
	private Deque<Circle> circleList;
	private int maxCircles = 5;
	private int thresholdRadius = 50;
	private int circleRadius = 50;
	public static final int paddingFromTop = 150; // height of the top panel
	public static final String PROPERTY_NAME_EYETHREAD_ERROR = "eye tracking thread error";
	public static final String PROPERTY_NAME_EMOTIONTHREAD_ERROR = "eye emotion thread error";
	private static final int TIMEOUT_IN_MS = 500;
	private static final Blackboard INSTANCE = new Blackboard();
	
	private Blackboard() {
    // Todo: super(new Object(0));
		eyeTrackingQueue = new LinkedBlockingQueue<>();
		emotionQueue = new LinkedBlockingQueue<>();
		processedDataQueue = new ConcurrentLinkedQueue<>();
		circleList = new ConcurrentLinkedDeque<>();
	}
	
	public static Blackboard getInstance() {
		return INSTANCE;
	}
	
	public void addToEyeTrackingQueue(String data) throws InterruptedException {
		eyeTrackingQueue.put(data);
	}
	
	public String pollEyeTrackingQueue() throws InterruptedException {
		return eyeTrackingQueue.poll(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS);
	}
	
	public void addToEmotionQueue(String data) throws InterruptedException {
		emotionQueue.put(data);
	}
	
	public String pollEmotionQueue() throws InterruptedException {
		return emotionQueue.poll(TIMEOUT_IN_MS, TimeUnit.MILLISECONDS);
	}
	
	public void addToProcessedDataQueue(ProcessedDataObject data) {
		processedDataQueue.add(data);
		changeSupport.firePropertyChange(PROPERTY_NAME_PROCESSED_DATA, null, null);
	}
	
	public ProcessedDataObject getFromProcessedDataObjectQueue() {
		return processedDataQueue.poll();
	}
	
	public Deque<Circle> getCircleList() {
		return circleList;
	}
	
	public void setCircleList(Deque<Circle> circleList) {
		this.circleList = circleList;
		changeSupport.firePropertyChange(PROPERTY_NAME_VIEW_DATA, null, null);
	}
	
	public String getFormattedConnectionSettings() {
		return String.format(
			"""
				\t\tEye Tracking Socket IP: %s:%s
				\t\tEmotion Tracking Socket IP: %s:%s
				""",
			eyeTrackingSocket_Host, eyeTrackingSocket_Port,
			emotionSocket_Host, emotionSocket_Port);
	}
	
	public String getEyeTrackingSocket_Host() {
		return eyeTrackingSocket_Host;
	}
	
	public int getEyeTrackingSocket_Port() {
		return eyeTrackingSocket_Port;
	}
	
	public String getEmotionSocket_Host() {
		return emotionSocket_Host;
	}
	
	public int getEmotionSocket_Port() {
		return emotionSocket_Port;
	}
	
	public void setEyeTrackingSocket_Host(String eyeTrackingSocket_Host) {
		this.eyeTrackingSocket_Host = eyeTrackingSocket_Host;
	}
	
	public void setEyeTrackingSocket_Port(int eyeTrackingSocket_Port) {
		this.eyeTrackingSocket_Port = eyeTrackingSocket_Port;
	}
	
	public void setEmotionSocket_Host(String emotionSocket_Host) {
		this.emotionSocket_Host = emotionSocket_Host;
	}
	
	public void setEmotionSocket_Port(int emotionSocket_Port) {
		this.emotionSocket_Port = emotionSocket_Port;
	}
	
	//Todo: This looks suspicious. Why is this method here?
	public void addChangeSupportListener(String propertyName, PropertyChangeListener pcl) {
		changeSupport.addPropertyChangeListener(propertyName, pcl);
	}
	
	public int getMaxCircles() {
		return maxCircles;
	}
	
	public void setMaxCircles(int maxCircles) {
		this.maxCircles = maxCircles;
	}
	
	public int getThresholdRadius() {
		return thresholdRadius;
	}
	
	public void setThresholdRadius(int thresholdRadius) {
		this.thresholdRadius = thresholdRadius;
	}
	
	//Todo: This looks suspicious. Why is this method here?
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
		changeSupport.removePropertyChangeListener(propertyName, pcl);
	}
	
	public void reportEyeThreadError(String ex_message) {
		changeSupport.firePropertyChange(PROPERTY_NAME_EYETHREAD_ERROR, null, ex_message);
	}
	
	public void reportEmotionThreadError(String ex_message) {
		changeSupport.firePropertyChange(PROPERTY_NAME_EMOTIONTHREAD_ERROR, null, ex_message);
	}
	
}