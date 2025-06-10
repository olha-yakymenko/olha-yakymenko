package com.example;

import java.util.Arrays;

public class CartInvoker {
    private Command[] commandHistory = new Command[10]; 
    private Command[] undoneCommands = new Command[10];
    private int historySize = 0; 
    private int undoneSize = 0;

    public void executeCommand(Command command) {
        command.execute();

        if (historySize >= commandHistory.length) {
            commandHistory = Arrays.copyOf(commandHistory, commandHistory.length * 2);
        }
        commandHistory[historySize++] = command;

        // Resetujemy historię cofniętych komend, gdy dodamy nową
        Arrays.fill(undoneCommands, null);
        undoneSize = 0;
    }

    public void undoLastCommand() {
        if (historySize > 0) {
            Command command = commandHistory[--historySize];
            command.undo();

            if (undoneSize >= undoneCommands.length) {
                undoneCommands = Arrays.copyOf(undoneCommands, undoneCommands.length * 2);
            }
            undoneCommands[undoneSize++] = command;
            commandHistory[historySize] = null;  // Usuwamy operację z historii
        }
    }

    public void redoLastUndoneCommand() {
        if (undoneSize > 0) {
            Command command = undoneCommands[--undoneSize]; 
            command.execute();

            if (historySize >= commandHistory.length) {
                commandHistory = Arrays.copyOf(commandHistory, commandHistory.length * 2);
            }
            commandHistory[historySize++] = command;
            undoneCommands[undoneSize] = null; // Usuwamy operację z redo
        }
    }

    public void clearHistory() {
        Arrays.fill(commandHistory, null);
        Arrays.fill(undoneCommands, null);
        historySize = 0;
        undoneSize = 0;
    }
}
