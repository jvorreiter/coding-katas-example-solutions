package algodat.ii_algorithms_for_realistic_problems;

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

class Resource {
    private final String identifier;

    public Resource(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}

class Process {
    private final Resource[] requiredResources;
    private final int id;

    private int startTime;
    private final int executionTime;

    public Process(int id, int startTime, int executionTime, Resource... requiredResources) {
        this.id = id;
        this.startTime = startTime;
        this.executionTime = executionTime;
        this.requiredResources = requiredResources;
    }

    public Resource[] getRequiredResources() {
        return requiredResources;
    }

    public int getId() {
        return id;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public int getEndTime() {
        return this.startTime + this.executionTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}

class ProcessResourceScheduler {

    /**
     * Plant die Prozesse so, dass immer der Prozess mit dem nächstmöglichen <i>tatsächlichen</i> <u>Start</u>zeitpunkt gestartet wird.
     * @param processes
     * @return
     */
    public List<Process> scheduleProcesses_byActualStartTime(List<Process> processes) {
        return scheduleProcesses_byActualTime(processes, p -> p.getStartTime());
    }

    /**
     * Plant die Prozesse so, dass immer der Prozess mit dem nächstmöglichen <i>tatsächlichen</i> <u>End</u>zeitpunkt gestartet wird.
     * @param processes
     * @return
     */
    public List<Process> scheduleProcesses_byActualEndTime(List<Process> processes) {
        return scheduleProcesses_byActualTime(processes, p -> p.getEndTime());
    }

    private List<Process> scheduleProcesses_byActualTime(List<Process> processes, Function<Process, Integer> processTimeSelector) {
        var resources = getResources(processes);

        // Map, die pro Ressource den nächsten Zeitpunkt enthält, an dem die Ressource wieder verwendet werden kann
        Map<Resource, Integer> resourceAvailableAt = resources.stream()
                .collect(Collectors.toMap(r -> r, r -> 0));

        // Map, die pro Ressource den nächsten Zeitpunkt enthält, an dem die Ressource wieder verwendet werden kann
        Map<Resource, ArrayList<Process>> resourcesUsedByProcess = resources.stream()
                .collect(Collectors.toMap(
                        resource -> resource,
                        resource -> new ArrayList<>(processes.stream()
                                .filter(p -> Arrays.stream(p.getRequiredResources()).anyMatch(r -> r.equals(resource)))
                                .toList())
                ));

        // in der PriorityQueue werden die Elemente anhand ihrer Endzeit sortiert
        // jees Mal, wenn ein neuer Prozess geplant wurde, aktualisieren wir die Start- und Endzeit nachfolgender Prozesse,
        // die die gleichen Ressourcen (bzw. einen Teil davon) benötigen.
        // Dabei aktualisieren wir auch die Reihefolge in dieser Queue, um immer den Prozess mit der nächsten Start-/Endzeit
        // an vorderster Stelle zu haben.
        var processQueue = new PriorityQueue<>(Comparator.comparingInt(processTimeSelector::apply));
        processQueue.addAll(processes);

        var scheduledProcessList = new ArrayList<Process>();

        while(!processQueue.isEmpty()) {
            var nextProcess = processQueue.poll();
            scheduledProcessList.add(nextProcess);

            for(var resource : nextProcess.getRequiredResources()) {
                // früheste Freigabezeitpunkte für die benötigten Ressourcen aktualisieren
                resourceAvailableAt.put(resource, nextProcess.getEndTime());

                // Prozess aus den Listen der Prozesse nach Ressource entfernen, da er nicht mehr neu eingeplant werden muss
                resourcesUsedByProcess.get(resource).remove(nextProcess);

                // früheste Startzeitpunkte betroffener anderer Prozesse und Reihenfolge in der PriorityQueue aktualisieren
                for(var affectedProcess : resourcesUsedByProcess.get(resource)) {
                    // betroffener Prozess kann/soll erst nach Endzeitpunkt des neu geplanten Prozesses gestartet werden,
                    // hier muss also nichts aktualisiert werden
                    if(affectedProcess.getStartTime() >= nextProcess.getEndTime()) {
                        continue;
                    }

                    affectedProcess.setStartTime(nextProcess.getEndTime());
                    // entfernen und danach wieder hinzufügen -> Prozess wird nach neuer Endzeit neu eingeordnet
                    // ist benötigt, da es keine update-Methode gibt
                    processQueue.remove(affectedProcess);
                    processQueue.add(affectedProcess);
                }
            }
        }

        return scheduledProcessList;
    }


    /**
     * Plant die Prozesse nach einem sehr simplen Verhalten:
     * Prozesse, werden nach ihrer gewünschten <u>Start</u>zeit priorisiert, und werden der Reihe nach gestartet,
     * sobald die benötigten Ressourcen frei und die gewünschte Startzeit erreicht wurde.
     *
     * @param processes
     * @return
     */
    public List<Process> scheduleProcesses_simpleByStartTime(List<Process> processes) {
        return scheduleProcesses_simple(processes, p -> p.getStartTime());
    }

    /**
     * Plant die Prozesse nach einem sehr simplen Verhalten:
     * Prozesse, werden nach ihrer gewünschten <u>End</u>zeit priorisiert, und werden der Reihe nach gestartet,
     * sobald die benötigten Ressourcen frei und die gewünschte Startzeit erreicht wurde.
     *
     * @param processes
     * @return
     */
    public List<Process> scheduleProcesses_simpleByEndTime(List<Process> processes) {
        return scheduleProcesses_simple(processes, p -> p.getEndTime());
    }

    private ArrayList<Process> scheduleProcesses_simple(List<Process> processes, Function<Process, Integer> processTimeSelector) {
        var resources = getResources(processes);

        // Map, die pro Ressource den nächsten Zeitpunkt enthält, an dem die Ressource wieder verwendet werden kann
        Map<Resource, Integer> resourceAvailableAt = resources.stream()
                .collect(Collectors.toMap(r -> r, r -> 0));

        // hier werden die Prozesse nach ihrer gewünschten Start- bzw. Endzeit sortiert
        var processesSortedBySelectedTime = processes.stream()
                .sorted(Comparator.comparingInt(p -> processTimeSelector.apply(p)))
                .toList();

        var scheduledProcessList = new ArrayList<Process>();
        for (var process : processesSortedBySelectedTime) {
            // frühester Zeitpunkt, zu dem alle benötigten Ressourcen frei sind
            var resourcesAvailableAt = Arrays.stream(process.getRequiredResources())
                    .mapToInt(r -> resourceAvailableAt.get(r))
                    .max()
                    .orElse(0);

            // Zeitpunkt gleich oder nach process.startTime, zu dem alle benötigten Ressourcen frei sind
            var actualStartTime = Math.max(resourcesAvailableAt, process.getStartTime());

            // Prozess mit neuem geplanten Zeitpunkt für Rückgabe hinzufügen
            process.setStartTime(actualStartTime);
            scheduledProcessList.add(process);

            // früheste Freigabezeitpunkte für die benötigten Ressourcen aktualisieren
            Arrays.stream(process.getRequiredResources())
                    .forEach(resource -> resourceAvailableAt.put(resource, process.getEndTime()));
        }

        return scheduledProcessList;
    }

    /**
     * Gibt ein Set mit allen Ressourcen zurück, die von den übergebenen Prozessen benötigt werden.
     *
     * @param processes
     * @return
     */
    private static Set<Resource> getResources(List<Process> processes) {
        return processes.stream()
                .flatMap(p -> Arrays.stream(p.getRequiredResources()))
                .collect(Collectors.toSet());
    }
}