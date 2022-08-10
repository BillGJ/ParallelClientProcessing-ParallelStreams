import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public final class SummarizeClients {
    public static void main(String[] args) throws Exception {

        List<UdacisearchClient> clients = ClientStore.getClients();
        int numClients = clients.size();

        // Done: Create a ForkJoinPool to use as a thread pool.
        int processorNumber = Runtime.getRuntime().availableProcessors();
        ForkJoinPool forkJoinPool = new ForkJoinPool(processorNumber);

        // Done: For each metric below, turn it into a Future and submit it to the ForkJoinPool.
        Future<Integer> totalQuarterlySpend =
                forkJoinPool.submit(() -> clients
                        .parallelStream()
                        .mapToInt(UdacisearchClient::getQuarterlyBudget)
                        .sum());

        Future<Double> averageBudget =
                forkJoinPool.submit(() -> clients
                        .parallelStream()
                        .mapToDouble(UdacisearchClient::getQuarterlyBudget)
                        .average()
                        .orElse(0));

        Future<Long> nextExpiration =
                forkJoinPool.submit(() -> clients
                        .parallelStream()
                        .min(Comparator.comparing(UdacisearchClient::getContractEnd))
                        .map(UdacisearchClient::getId)
                        .orElse(-1L));

        Future<List<ZoneId>> representedZoneIds =
                forkJoinPool.submit(() -> clients
                        .parallelStream()
                        .flatMap(c -> c.getTimeZones().stream())
                        .distinct()  // Or use Collectors.toSet()
                        .collect(Collectors.toList()));

        Future<Map<Year, Long>> contractsPerYear =
                forkJoinPool.submit(()->clients
                        .parallelStream()
                        .collect(Collectors.groupingByConcurrent(SummarizeClients::getContractYear, Collectors.counting())));

        forkJoinPool.shutdown();

        System.out.println("Num clients: " + numClients);

        // Done: You will need to call Future#get() on each of the futures to return the actual
        //       computed value.
        System.out.println("Total quarterly spend: " + totalQuarterlySpend.get());
        System.out.println("Average budget: " + averageBudget.get());
        System.out.println("ID of next expiring contract: " + nextExpiration.get());
        System.out.println("Client time zones: " + representedZoneIds.get());
        System.out.println("Contracts per year: " + contractsPerYear.get());
    }

    private static Year getContractYear(UdacisearchClient client) {
        LocalDate contractDate =
                LocalDate.ofInstant(client.getContractStart(), client.getTimeZones().get(0));
        return Year.of(contractDate.getYear());
    }
}