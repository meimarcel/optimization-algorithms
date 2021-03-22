/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author meimarcel
 */
public class Logger {
    public final String ANSI_RESET = "\u001b[0m";
    public final String ANSI_BLACK = "\u001b[30m";
    public final String ANSI_RED = "\u001b[31m";
    public final String ANSI_GREEN = "\u001b[32m";
    public final String ANSI_YELLOW = "\u001b[33m";
    public final String ANSI_BLUE = "\u001B[34m";
    public final String ANSI_PURPLE = "\u001b[35m";
    public final String ANSI_CYAN = "\u001b[36m";
    public final String ANSI_WHITE = "\u001b[37m";
    
    public String header() {
        StringBuilder header = new StringBuilder();
        System.out.print(this.ANSI_GREEN);
        header.append("    #########       #########         ####\n");
        header.append("    #        #     #                #     #\n");
        header.append("    #         #   #                #       #\n");
        header.append("    #         #   #               #         #\n");
        header.append("    #        #     #             #           #\n");
        header.append("    ########        #######      #           #\n");
        header.append("    #                      #     #           #\n");
        header.append("    #                       #     #         #\n");
        header.append("    #                       #      #       #\n");
        header.append("    #                      #        #     #\n");
        header.append("    #             #########          ####\n");
        header.append("---------------------------------------------------------\n");
        header.append("Developed by Mei Marcel - Email: mei.marcel05@gmail.com\n");
        header.append("---------------------------------------------------------\n");
        header.append(" ARGUMENTS LIST\n");
        header.append("\n");
        header.append("-PlotGraph=true | To save the plotted graphs.\n");
        header.append("-SaveLog=true | To save all the logs.\n");
        System.out.print(header);
        System.out.println(this.ANSI_RESET);
        
        return header.toString();
    }
    
    public String info(String message) {
        String msg = "[INFO] "+message;
        System.out.print(this.ANSI_GREEN+msg+this.ANSI_RESET);
        return msg;
    }
    
    public String error(String message) {
        String msg = "[ERROR] "+message;
        System.out.print(this.ANSI_RED+msg+this.ANSI_RESET);
        return msg;
    }
    
    public String warning(String message) {
        String msg = "[WARNING] "+message;
        System.out.print(this.ANSI_YELLOW+msg+this.ANSI_RESET);
        return msg;
    }
    
    public String message(String message) {
        String msg = "[MESSAGE] "+message;
        System.out.print(this.ANSI_CYAN+msg+this.ANSI_RESET);
        return msg;
    }
    
    public String white(String message) {
        System.out.print(this.ANSI_WHITE+message+this.ANSI_RESET);
        return message;
    }
}
