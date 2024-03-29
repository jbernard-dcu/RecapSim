//* Title:        CloudSim Toolkit. Moved here as it was discontinued in new versions
package eu.recap.sim.helpers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.Objects;

/**
* Logger used for performing logging of the simulation process. It provides the
* ability to substitute the output stream by any OutputStream subclass.
*
* @author Anton Beloglazov
* @since CloudSim Toolkit 2.0
*/
public final class Log {
   /**
    * An enum that may be used to define the level (type)
    * of a log message.
    */
   public enum Level {
       INFO,
       ERROR,

       /**
        * A log level for messages that will be shown only when the application
        * is run in debug mode.
        */
       DEBUG}

   /**
    * The Constant LINE_SEPARATOR.
    */
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   /**
    * The stream where the log will the outputted.
    */
   private static OutputStream output;

   /**
    * Indicates if the logger is disabled or not. If set to true, the call for
    * any print method has no effect.
    */
   private static boolean disabled;

   /**
    * Buffer to avoid creating new string builder upon every print.
    */
   private static StringBuilder buffer = new StringBuilder();

   /**
    * Checks if application is running in debug mode.
    * "jdwp" is the acronym for "Java Debug Wire Protocol" that
    * may exists as an application parameter to define
    * the application is running in debug mode.
    *
    * @see #isDebug()
    */
   private static boolean debug =
           ManagementFactory.getRuntimeMXBean().getInputArguments()
                   .toString().indexOf("jdwp") > 0;

   /**
    * A private constructor to avoid class instantiation.
    */
   private Log(){}

   /**
    * Prints a message.
    *
    * @param message the message
    */
   public static void print(String message) {
       if (isEnabled()) {
           try {
               getOutput().write(message.getBytes());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }

   /**
    * Prints the message passed as a non-String object.
    *
    * @param message the message
    */
   public static void print(Object message) {
       print(String.valueOf(message));
   }

   /**
    * Prints a message and a new line.
    *
    * @param message the message
    */
   public static void printLine(String message) {
       print(message + LINE_SEPARATOR);
   }

   /**
    * Prints an empty line.
    */
   public static void printLine() {
       print(LINE_SEPARATOR);
   }

   /**
    * Prints the message passed as a non-String object and a new line.
    *
    * @param message the message
    */
   public static void printLine(Object message) {
       printLine(String.valueOf(message));
   }

   /**
    * Prints the concatenated text representation of the arguments.
    *
    * @param messages the messages to print
    */
   public static void printConcat(Object... messages) {
       if (isEnabled()) {
           buffer = new StringBuilder(messages.length);
           for (Object message : messages) {
               buffer.append(String.valueOf(message));
           }
           print(buffer);
       }
   }

   /**
    * Prints the concatenated text representation of the arguments and a new
    * line.
    *
    * @param messages the messages to print
    */
   public static void printConcatLine(Object... messages) {
       if (isEnabled()) {
           buffer = new StringBuilder(messages.length);
           for (Object message : messages) {
               buffer.append(String.valueOf(message));
           }
           printLine(buffer);
       }
   }

   /**
    * Prints a string formated as in String.printFormatted().
    *
    * @param format the printFormatted
    * @param args the args
    */
   public static void printFormatted(String format, Object... args) {
       print(String.format(format, args));
   }

   /**
    * Prints a string formated as in String.printFormatted(), followed by a new
    * line.
    *
    * @param format the printFormatted
    * @param args the args
    */
   public static void printFormattedLine(String format, Object... args) {
       printLine(String.format(format, args));
   }

   /**
    * Prints a string formated as in String.printFormatted(), followed by a new
    * line, that will be printed only according to
    * the specified level
    *
    * @param level the level that define the kind of message
    * @param klass Class that is asking to print a message (where the print method
    * is being called)
    * @param time current simulation time
    * @param format the printFormatted
    * @param args the args
    */
   public static void println(Level level, Class klass, double time, String format, Object... args) {
       if((level == Level.DEBUG && isDebug()) || (level != Level.DEBUG)){
           String msg = String.format(format, args);
           printFormattedLine("Time %.1f %s/%s\n   %s", time, level.name(), klass.getSimpleName(), msg);
       }
   }

   /**
    * Sets the output stream.
    *
    * @param newOutput the new output
    */
   public static void setOutput(OutputStream newOutput) {
       output = newOutput;
   }

   /**
    * Gets the output stream.
    *
    * @return the output
    */
   public static OutputStream getOutput() {
       if (Objects.isNull(output)) {
           setOutput(System.out);
       }
       return output;
   }

   /**
    * Sets the disable output flag.
    *
    * @param disable the new disabled
    */
   public static void setDisabled(boolean disable) {
       disabled = disable;
   }

   /**
    * Checks if the output is disabled.
    *
    * @return true, if it is disable
    */
   public static boolean isDisabled() {
       return disabled;
   }

   /**
    * Checks if the output is enabled.
    *
    * @return true, if it is enable
    */
   public static boolean isEnabled() {
       return !disabled;
   }

   /**
    * Disables the output.
    */
   public static void disable() {
       setDisabled(true);
   }

   /**
    * Enables the output.
    */
   public static void enable() {
       setDisabled(false);
   }

   public static boolean isDebug() {
       return debug;
   }

}
