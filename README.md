# Parallel Client Processing - Exercise: Parallel Streams

This program's goal is to do its computations in *parallel*,
using a thread pool, parallel `Stream`s, and `Future`s.

We supposed that and old `SummarizeClients` class was taking longer and longer to process a lot of clients
and generate its summary, so we use `Parallel Streams` concepts to solve that issue.

# Compiling and Running

    javac -cp . SummarizeClients.java
    java -cp . SummarizeClients

You should notice there are 10,000 clients now. 
If you want, take a peek in `ClientStore.java` to see how they are randomly generated using the Stream API.



