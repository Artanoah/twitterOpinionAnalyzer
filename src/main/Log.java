package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Dies ist eine einfache Log-Klasse mit grundlegenden logging-Methoden.
 * Die Erstellte Logdatei entspricht dem <code>logName</code>.txt aus dem
 * Constructor. Alle Methoden dieser Klasse sind synchronized, sodass ein
 * Log beispielsweise problemlos von mehreren Threads gleichzeitig oder 
 * in parallelen Streams verwendet werden kann.
 * 
 * @author Steffen Giersch
 */
public class Log {
	private String logName;
	private String fileName;
	private PrintWriter writer;
	
	/**
	 * Gibt ein neues Log-Objekt zurueck und erstellt eine Logdatei mit dem
	 * Namen "[logName].txt".
	 * 
	 * @param logName <code>String</code> Name der Logdatei ohne Dateiendung
	 */
	public Log(String logName) {
		this.logName = logName;
		this.fileName = logName + ".txt";
		
		try {
			writer = new PrintWriter(fileName);
		} catch (IOException e) {
			System.err.println("LogFile konnte nicht erstellt werden");
		}
	}
	
	/**
	 * Erstellt eine neue Info in der Logdatei. Die neu erstellt Nachricht wird
	 * ans Ende der Datei angehaengt und mit einem Timestamp versehen.
	 * 
	 * @param message <code>String</code> Zu loggende Nachricht.
	 */
	public synchronized void newInfo(String message) {
		String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		writer.println(now);
		writer.println("Info: " + message);
		writer.flush();
	}
	
	/**
	 * Erstellt eine neue Warnung in der Logdatei. Die neu erstellt Nachricht 
	 * wird ans Ende der Datei angehaengt und mit einem Timestamp versehen.
	 * 
	 * @param message <code>String</code> Zu loggende Nachricht.
	 */
	public synchronized void newWarning(String message) {
		String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		writer.println(now);
		writer.println("Warning: " + message);
		writer.flush();
	}
	
	/**
	 * Gibt den Namen des Logs zurueck.
	 * 
	 * @return <code>String</code> Name des Logs.
	 */
	public String getLogName() {
		return logName;
	}
}
