import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<Callable<Boolean>> tasks = new ArrayList<>();
        int numberOfThreads = 1000;
        for (int i = 0; i < numberOfThreads; i++) {
            tasks.add(() -> {
                String route = generateRoute("RLRFR", 100);
                int count = (int) (route
                        .chars()
                        .filter(s -> s == 'R')
                        .count());
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(count)) {
                        sizeToFreq.computeIfPresent(count, (k, v) -> v + 1);
                    } else {
                        sizeToFreq.compute(count, (k, v) -> v = 1);
                    }
                }
                return true;
            });
        }
        threadPool.invokeAll(tasks);
        threadPool.shutdown();
        printSizeToFrequency(sizeToFreq);
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void printSizeToFrequency(Map<Integer, Integer> map) {
        if (map.isEmpty()) {
            System.out.println("Значения отсутствуют");
        } else {
            final int maxValue = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getValue();
            final Set<Map.Entry<Integer, Integer>> sizesWithMaxFrequency = new HashSet<>();
            final Set<Map.Entry<Integer, Integer>> otherSizes = new HashSet<>();
            map.entrySet().forEach(s -> {
                if (s.getValue().equals(maxValue)) {
                    sizesWithMaxFrequency.add(s);
                } else {
                    otherSizes.add(s);
                }
            });
            System.out.println("Самое частое количество повторений:");
            sizesWithMaxFrequency.forEach(s -> {
                System.out.printf("%d %s %d %s%n", s.getKey(), "встретилось", s.getValue(), "раз");
            });
            if (otherSizes.size() > 0) {
                System.out.println("Другие размеры:");
                otherSizes.forEach(s -> {
                    System.out.printf("- %d (%d %s)%n", s.getKey(), s.getValue(), "раз");
                });
            }
        }
    }
}