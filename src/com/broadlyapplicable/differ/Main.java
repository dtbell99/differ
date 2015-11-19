package com.broadlyapplicable.differ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dbell
 */
public class Main {

    private final Map<Integer, String> fileOneMap;
    private final Map<Integer, String> fileTwoMap;
    private final Map<Integer, String> newLines;
    private final Map<Integer, String> removedLines;
    private static final String ADDED_COLOR = "#43BFC7";
    private static final String REMOVED_COLOR = "#FBB917";
    private static final String TR_BACKGROUND = "#f1f1f1"; // #E5E4E2";

    public Main() {
        fileOneMap = new HashMap<>();
        fileTwoMap = new HashMap<>();
        newLines = new HashMap<>();
        removedLines = new HashMap<>();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Differ.jar NewFile.txt PreviousFile.txt");
            System.exit(-1);
        }

        File fileOne = new File(args[0]);
        File fileTwo = new File(args[1]);

        Main main = new Main();
        main.fileExists(fileOne, args[0]);
        main.fileExists(fileTwo, args[1]);
        main.compare(fileOne, fileTwo);
    }

    private void compare(File fileOne, File fileTwo) {
        processLines(fileOneMap, fileOne);
        processLines(fileTwoMap, fileTwo);
        processMaps();
        List<Change> changeList = createChangeCollection();
        Collections.sort(changeList);
        generateReport(changeList, fileOne.getName(), fileTwo.getName());
    }

    private List<Change> createChangeCollection() {
        List<Change> changeList = new ArrayList<>();
        updateChangeList(changeList, newLines, true, false);
        updateChangeList(changeList, removedLines, false, true);
        return changeList;
    }

    private void updateChangeList(List<Change> changeList, Map<Integer, String> map, boolean added, boolean removed) {
        for (int lneNbr : map.keySet()) {
            String lne = map.get(lneNbr);
            Change change = new Change(lne, lneNbr, added, removed);
            changeList.add(change);
        }
    }

    private void generateReport(List<Change> changeList, String newFile, String oldFile) {
        StringBuilder html = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mma");
        String css = "<style type=\"text/css\">body {font-family:arial;} table {border-collapse: collapse; border:.15em solid cadetblue;} th {background-color:" + TR_BACKGROUND + "; padding:15px;} td {border:.1em solid #eeeeee; padding:15px;}</style>";
        html.append("<html><head>").append(css).append("</head><body>");
        //html.append("<h2>File Diff</h2>");
        String backgroundTD = "<td style=\"background-color:" + TR_BACKGROUND + ";\">";
        html.append("<table>");
        html.append("<tr><td align=\"center\" colspan=\"2\" style=\"background-color:#dddddd;\">Diff Tool</td></tr>");
        html.append("<tr>");
        html.append(backgroundTD);
        html.append("<b>New File: </b></td><td>").append(newFile).append("</td></tr><tr>");
        html.append(backgroundTD);
        html.append("<b>Existing File:</b></td><td> ").append(oldFile).append("</td></tr>");
        String generated = "<tr><td style=\"background-color:" + TR_BACKGROUND + ";\"><b>Generated on:</b></td><td>" + sdf.format(new Date()) + "</td></tr>";
        html.append(generated);
        html.append("</table>");
        html.append("<br/><br/><table>");
        html.append("<tr><th>+/-</th><th>Line#</th><th>Line</th></tr>");
        updateRow(changeList, html);
        html.append("</table>");
        String reportName = newFile + "_vs_" + oldFile + ".html";
        writeFile(html, reportName);
    }

    private void writeFile(StringBuilder contents, String fileName) {
        File reportFile = new File(fileName);
        try (PrintWriter writer = new PrintWriter(fileName)) {
            reportFile.createNewFile();
            writer.println(contents.toString());
            System.out.println("File: " + fileName + " written");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateRow(List<Change> changeList, StringBuilder html) {
        String color = "black";
        String sign = "?";
        for (Change change : changeList) {
            if (change.isRemoved()) {
                color = REMOVED_COLOR;
                sign = "-";
            } else if (change.isAdded()) {
                color = ADDED_COLOR;
                sign = "+";
            }
            int lneNumber = change.getLineNumber();
            String lne = change.getLine();
            html.append("<tr><td align=\"center\" style=\"color:").append(color).append(";\">").append(sign).append("</td><td align=\"right\" style=\"color:").append(color).append(";\">").append(lneNumber).append("</td><td style=\"color:").append(color).append(";\">").append(lne).append("</td></tr>");
        }

    }

    private void processMaps() {
        for (int lineNumber : fileOneMap.keySet()) {
            String lne = fileOneMap.get(lineNumber);
            boolean found = testLine(lne, fileTwoMap);
            if (!found) {
                newLines.put(lineNumber, lne);
            }
        }

        for (int lineNumber : fileTwoMap.keySet()) {
            String lne = fileTwoMap.get(lineNumber);
            boolean found = testLine(lne, fileOneMap);
            if (!found) {
                removedLines.put(lineNumber, lne);
            }
        }
    }

    private boolean testLine(String lne, Map<Integer, String> map) {
        for (int lineNumber : map.keySet()) {
            String testLine = map.get(lineNumber);
            if (lne.equals(testLine)) {
                return true;
            }
        }
        return false;
    }

    private void processLines(Map<Integer, String> map, File file) {
        int lineCounter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String lne;
            while ((lne = br.readLine()) != null) {
                lne = lne.trim();
                if (lne != null && !lne.isEmpty()) {
                    lineCounter++;
                    map.put(lineCounter, lne);
                }
            }
        } catch (IOException iox) {
            iox.printStackTrace();
            System.exit(-1);
        }
    }

    private void fileExists(File file, String fName) {
        if (!file.exists()) {
            System.out.println(fName + " does not exist");
            System.exit(-1);
        }
    }

}
