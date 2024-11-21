package algodat.ii_algorithms_for_realistic_problems.process_resource_scheduling;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProcessResourceSchedulingMain {
    public static void main(String[] args) {
        var scheduler = new ProcessResourceScheduler();

        writeSchedulingOutput("simpel, nach gewünschter Startzeit",
                scheduler.scheduleProcesses_simpleByStartTime(getTestProcesses()));
        writeSchedulingOutput("simpel, nach gewünschter Endzeit",
                scheduler.scheduleProcesses_simpleByEndTime(getTestProcesses()));

        writeSchedulingOutput("nach tatsächlicher Startzeit",
                scheduler.scheduleProcesses_byActualStartTime(getTestProcesses()));
        writeSchedulingOutput("nach tatsächlicher Endzeit",
                scheduler.scheduleProcesses_byActualEndTime(getTestProcesses()));
    }

    private static List<Process> getTestProcesses() {
        var resourceA = new Resource("A");
        var resourceB = new Resource("B");
        var resourceC = new Resource("C");

        var process1 = new Process(1, 0, 6, resourceA);
        var process2 = new Process(2, 1, 2, resourceB);
        var process3 = new Process(3, 2, 6, resourceA, resourceB);
        var process4 = new Process(4, 3, 2, resourceB);
        var process5 = new Process(5, 4, 4, resourceC);

        var processes = List.of(process1, process2, process3, process4, process5);
        return processes;
    }

    private static void writeSchedulingOutput(String title, List<Process> scheduledProcesses) {
        System.out.println("Prozessplanung: " + title);

        // Prozesse für Output nach ID sortieren
        var sortedProcesses = scheduledProcesses.stream()
                .sorted(Comparator.comparingInt(Process::getId))
                .toList();

        for (var process : sortedProcesses) {
            var line = String.format("P%02d: ", process.getId());
            if (process.getStartTime() > 0) {
                line += String.format("%" + process.getStartTime() + "s", "");
            }
            line += String.format("%" + process.getExecutionTime() + "s", "").replace(' ', '#');

            System.out.println(line);
        }

        System.out.println();
    }
}

