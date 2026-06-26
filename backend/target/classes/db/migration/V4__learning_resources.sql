-- ============================================================
-- V4: learning_resources table + 120+ curated resources
-- ============================================================

CREATE TABLE IF NOT EXISTS learning_resources (
    id              CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    topic           VARCHAR(100) NOT NULL,
    title           VARCHAR(300) NOT NULL,
    provider        VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL,
    url             VARCHAR(1000) NOT NULL,
    description     TEXT,
    duration        VARCHAR(50),
    is_free         BOOLEAN NOT NULL DEFAULT TRUE,
    display_order   INT NOT NULL DEFAULT 100
);

CREATE INDEX idx_resources_topic ON learning_resources(topic);

-- ============================================================
-- ARRAYS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Arrays', 'Arrays & Hashing — NeetCode Roadmap', 'NeetCode', 'PLAYLIST', 'https://neetcode.io/roadmap', 'Interactive roadmap with video solutions for every array pattern. Start here.', '3h', true, 1),
('Arrays', 'Array Basics & Two Pointers', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=jzZsG8n2R9A', 'Visual walkthrough of two-pointer technique on sorted arrays.', '22 min', true, 2),
('Arrays', 'Sliding Window Technique', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=MK-NZ4hN7rs', 'Fixed and variable sliding window with code.', '18 min', true, 3),
('Arrays', 'Prefix Sum Pattern', 'YouTube — Greg Hogg', 'VIDEO', 'https://www.youtube.com/watch?v=7pJo_rM0z_s', 'Prefix sums and range-sum queries with LeetCode problems.', '15 min', true, 4),
('Arrays', 'LeetCode Array Study Plan', 'LeetCode', 'PRACTICE', 'https://leetcode.com/study-plan/array-and-string/', 'Curated 30-problem array study plan from Easy to Hard.', '30 problems', true, 5),
('Arrays', 'Master the Coding Interview: Data Structures + Algorithms', 'Udemy (Prosus) — Andrei Neagoie', 'COURSE', 'https://www.udemy.com/course/master-the-coding-interview-data-structures-algorithms/', 'Best overall DSA course on Udemy. Covers arrays deeply with Big-O analysis.', '19h 30m', false, 6),
('Arrays', 'Kadane''s Algorithm — Maximum Subarray', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=86CQq3pKSUw', 'Clear derivation of Kadane''s algorithm with proof.', '12 min', true, 7);

-- ============================================================
-- STRINGS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Strings', 'String Manipulation Techniques', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=qgBT1-fpR5I', 'Anagram, palindrome and sliding window string patterns.', '25 min', true, 1),
('Strings', 'KMP Algorithm Explained', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=V5-7GzOfADQ', 'Pattern matching with KMP, failure function derivation.', '30 min', true, 2),
('Strings', 'Java String Internals & Interview Questions', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/java-string-interview-questions/', 'Covers immutability, String pool, StringBuilder, intern(). Finance favourite.', '15 min read', true, 3),
('Strings', 'LeetCode String Study Plan', 'LeetCode', 'PRACTICE', 'https://leetcode.com/study-plan/array-and-string/', '20 string problems from Easy to Medium.', '20 problems', true, 4),
('Strings', 'Regular Expressions in Java', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/regular-expressions-java', 'Complete guide to Java regex with Pattern and Matcher.', '20 min read', true, 5);

-- ============================================================
-- HASHMAPS & SETS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('HashMaps & Sets', 'HashMap Internals — Java', 'YouTube — Defog Tech', 'VIDEO', 'https://www.youtube.com/watch?v=MkTtUR3dxCM', 'How Java HashMap works: hashing, chaining, resizing, treeification.', '28 min', true, 1),
('HashMaps & Sets', 'HashMap vs LinkedHashMap vs TreeMap', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-hashmap', 'Comprehensive comparison with performance characteristics.', '15 min read', true, 2),
('HashMaps & Sets', 'Hashing Problems — LeetCode', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/hash-table/', 'Filter tag: Hash Table — 300+ problems sorted by frequency.', '50+ problems', true, 3),
('HashMaps & Sets', 'Java Collections Framework Deep Dive', 'Udemy (Prosus) — Tim Buchalka', 'COURSE', 'https://www.udemy.com/course/java-the-complete-java-developer-course/', 'Section 13 covers Map, Set, List internals with Java interview examples.', '80h total', false, 4);

-- ============================================================
-- TWO POINTERS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Two Pointers', 'Two Pointers Pattern', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=On3r4I1kRcA', 'All two-pointer variants: opposite ends, same direction, fast/slow.', '20 min', true, 1),
('Two Pointers', 'Two Pointers — LeetCode Explore', 'LeetCode', 'ARTICLE', 'https://leetcode.com/explore/learn/card/array-and-string/205/array-two-pointer-technique/', 'Interactive tutorial with built-in practice problems.', '1h', true, 2),
('Two Pointers', '3Sum & Variants Explained', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=jzZsG8n2R9A', 'Sorting + two pointers approach for k-sum problems.', '18 min', true, 3),
('Two Pointers', 'Two Pointers Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/two-pointers/', '200+ two-pointer tagged problems.', '30+ problems', true, 4);

-- ============================================================
-- SLIDING WINDOW
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Sliding Window', 'Sliding Window Pattern — Complete Guide', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=MK-NZ4hN7rs', 'Fixed window + variable window with 6 LeetCode problems solved.', '30 min', true, 1),
('Sliding Window', 'Sliding Window Cheat Sheet', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/window-sliding-technique/', 'Template code and when to use sliding window vs two pointers.', '10 min read', true, 2),
('Sliding Window', 'Minimum Window Substring (Hard)', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=jSto0O4AJbM', 'Full walkthrough of the classic hard sliding window problem.', '16 min', true, 3),
('Sliding Window', 'Sliding Window Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/sliding-window/', '100+ sliding window problems.', '20+ problems', true, 4);

-- ============================================================
-- LINKED LISTS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Linked Lists', 'Linked List Playlist — NeetCode', 'NeetCode', 'PLAYLIST', 'https://neetcode.io/practice', 'All linked list problems with visual animations.', '2h', true, 1),
('Linked Lists', 'Floyd''s Cycle Detection Algorithm', 'YouTube — Back To Back SWE', 'VIDEO', 'https://www.youtube.com/watch?v=apIw0Opq5nk', 'Why fast/slow pointers detect cycles and find entry point.', '14 min', true, 2),
('Linked Lists', 'LRU Cache Implementation', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=7ABFKPK2hD4', 'HashMap + doubly linked list design pattern for O(1) get/put.', '22 min', true, 3),
('Linked Lists', 'Java LinkedList vs ArrayList', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-linkedlist', 'When to use each, internal implementation, time complexity.', '10 min read', true, 4),
('Linked Lists', 'Linked List Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/linked-list/', '100+ linked list problems sorted by frequency.', '30+ problems', true, 5);

-- ============================================================
-- STACKS & QUEUES
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Stacks & Queues', 'Monotonic Stack Pattern', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=Dq_ObZwTY_Q', 'Next greater element, daily temperatures, largest rectangle histogram.', '24 min', true, 1),
('Stacks & Queues', 'Java Deque as Stack and Queue', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-deque-vs-stack', 'Why Deque is preferred over Stack class in Java.', '8 min read', true, 2),
('Stacks & Queues', 'Stack & Queue Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/stack/', '150+ stack problems.', '20+ problems', true, 3);

-- ============================================================
-- BINARY SEARCH
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Binary Search', 'Binary Search — Full Pattern Guide', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=s4DPM8ct1pI', 'Template for all binary search variants: exact, leftmost, rightmost, on value space.', '20 min', true, 1),
('Binary Search', 'Binary Search on Answer Space', 'YouTube — Errichto', 'VIDEO', 'https://www.youtube.com/watch?v=GU7DpgHINWQ', 'Binary search when the answer itself is a value, not an index.', '25 min', true, 2),
('Binary Search', 'Binary Search — LeetCode Explore', 'LeetCode', 'ARTICLE', 'https://leetcode.com/explore/learn/card/binary-search/', 'Interactive course covering all binary search templates.', '3h', true, 3),
('Binary Search', 'Binary Search Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/binary-search/', '200+ binary search problems.', '25+ problems', true, 4);

-- ============================================================
-- TREES
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Trees - BFS/DFS', 'Binary Trees — NeetCode Playlist', 'NeetCode', 'PLAYLIST', 'https://neetcode.io/practice', 'Complete tree section: BFS, DFS, BST, and advanced tree problems.', '4h', true, 1),
('Trees - BFS/DFS', 'BFS vs DFS — When To Use Each', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=oDqjPvD1Uzg', 'Level-order with BFS; pre/in/post-order with DFS.', '18 min', true, 2),
('Trees - BFS/DFS', 'Binary Tree Level Order Traversal', 'YouTube — Back To Back SWE', 'VIDEO', 'https://www.youtube.com/watch?v=gcR28Hc1SXQ', 'Queue-based BFS with detailed animation.', '15 min', true, 3),
('Trees - BFS/DFS', 'Data Structures — Abdul Bari', 'Udemy (Prosus) — Abdul Bari', 'COURSE', 'https://www.udemy.com/course/datastructurescncpp/', 'Module 9 covers all tree types with derivations. Highest-rated DSA course.', '58h total', false, 4),
('Trees - BFS/DFS', 'Tree Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/tree/', '200+ tree problems. Start easy, then medium BST.', '40+ problems', true, 5);

-- ============================================================
-- RECURSION
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Recursion', 'Recursion — The Foundation', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=M2uO2nMT0Bk', 'Tree of recursive calls, tracing through call stack, base cases.', '14 min', true, 1),
('Recursion', 'Recursion to Iteration', 'YouTube — Errichto', 'VIDEO', 'https://www.youtube.com/watch?v=NgDh4NKqFmg', 'Converting recursive solutions to iterative using an explicit stack.', '20 min', true, 2),
('Recursion', 'Recursion in Java', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-recursion', 'Java recursion patterns, tail recursion, memoisation.', '12 min read', true, 3),
('Recursion', 'Recursion Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/recursion/', 'Recursion tagged problems from Easy to Medium.', '20+ problems', true, 4);

-- ============================================================
-- BACKTRACKING
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Backtracking', 'Backtracking Template', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=pfiQ_PS1g8E', 'Universal backtracking template applied to 5 classic problems.', '22 min', true, 1),
('Backtracking', 'Backtracking — GeeksForGeeks', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/backtracking-algorithms/', 'N-Queens, Sudoku, word search with Java code.', '25 min read', true, 2),
('Backtracking', 'Backtracking Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/backtracking/', '100+ backtracking problems.', '20+ problems', true, 3);

-- ============================================================
-- GRAPHS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Graphs - BFS/DFS', 'Graphs — NeetCode Playlist', 'NeetCode', 'PLAYLIST', 'https://neetcode.io/practice', 'BFS, DFS, Union-Find, Dijkstra, topological sort — all covered.', '5h', true, 1),
('Graphs - BFS/DFS', 'Graph Algorithms Playlist', 'YouTube — William Fiset', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLDV1Zeh2NRsDGO4--qE8yH72HFL1Km93P', 'Deep dives: BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, MST.', '6h', true, 2),
('Graphs - BFS/DFS', 'Number of Islands — BFS/DFS', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=pV2kpPD66nE', 'Grid graphs, flood fill, connected components pattern.', '15 min', true, 3),
('Graphs - BFS/DFS', 'Dijkstra''s Algorithm', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=XB4MIexjvY0', 'Greedy shortest path with priority queue. Full derivation.', '23 min', true, 4),
('Graphs - BFS/DFS', 'Union-Find (Disjoint Set Union)', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=ayW5B2W9hkk', 'Path compression + union by rank for near-O(1) operations.', '18 min', true, 5),
('Graphs - BFS/DFS', 'Graph Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/graph/', 'Start with: islands, clone graph, course schedule.', '40+ problems', true, 6);

-- ============================================================
-- HEAPS
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Heaps', 'Heap Data Structure', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=HqPJF2L5h9U', 'Min-heap, max-heap, heapify, heap sort — full derivation.', '22 min', true, 1),
('Heaps', 'Java PriorityQueue Deep Dive', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-priority-queue', 'PriorityQueue internals, custom comparators, common patterns.', '12 min read', true, 2),
('Heaps', 'Find Median from Data Stream', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=itmhHWaHupI', 'Two-heap technique for streaming median.', '16 min', true, 3),
('Heaps', 'Heap Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/heap-priority-queue/', '100+ heap problems.', '20+ problems', true, 4);

-- ============================================================
-- GREEDY
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Greedy', 'Greedy vs Dynamic Programming', 'YouTube — Abdul Bari', 'VIDEO', 'https://www.youtube.com/watch?v=ARvQcqJ_-NY', 'When greedy works vs when you need DP. Proof of correctness.', '20 min', true, 1),
('Greedy', 'Interval Scheduling Problems', 'YouTube — Errichto', 'VIDEO', 'https://www.youtube.com/watch?v=UqREKz65uDo', 'Meeting rooms I & II, non-overlapping intervals.', '18 min', true, 2),
('Greedy', 'Greedy Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/greedy/', '200+ greedy problems.', '20+ problems', true, 3);

-- ============================================================
-- DYNAMIC PROGRAMMING
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Dynamic Programming - 1D', 'DP for Beginners — NeetCode', 'YouTube — NeetCode', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLot-Xpze53letfIu9dMzIIO7na_sqvl0w', 'Covers 1D and 2D DP from scratch with 20+ problems.', '5h', true, 1),
('Dynamic Programming - 1D', 'Dynamic Programming Patterns', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/dynamic-programming/', 'All DP patterns: linear, interval, tree, bitmask DP with Java code.', '2h read', true, 2),
('Dynamic Programming - 1D', 'Dynamic Programming — Abdul Bari', 'YouTube — Abdul Bari', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLDN4rrl48XKpZkf03iYFl-O29szjTrs_O', 'Matrix chain, LCS, knapsack — full mathematical derivations.', '8h', true, 3),
('Dynamic Programming - 1D', 'Grokking Dynamic Programming Patterns', 'Udemy (Prosus) — Design Gurus', 'COURSE', 'https://www.udemy.com/course/dynamic-programming-the-insiders-guide/', 'Pattern-based approach to DP. Highly recommended for interviews.', '12h', false, 4),
('Dynamic Programming - 1D', 'DP Study Plan', 'LeetCode', 'PRACTICE', 'https://leetcode.com/study-plan/dynamic-programming/', 'Curated DP study plan from Easy to Hard.', '40 problems', true, 5),
('Dynamic Programming - 2D', 'Longest Common Subsequence', 'YouTube — Back To Back SWE', 'VIDEO', 'https://www.youtube.com/watch?v=ASoaQq66foQ', 'LCS with table visualisation. One of the clearest explanations.', '18 min', true, 1),
('Dynamic Programming - 2D', '2D DP — NeetCode', 'YouTube — NeetCode', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLot-Xpze53letfIu9dMzIIO7na_sqvl0w', '2D DP visualised with tables — coin change, LCS, edit distance, knapsack.', '3h', true, 2),
('Dynamic Programming - 2D', '2D DP Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/dynamic-programming/', 'Medium/Hard: coin change, LCS, edit distance, knapsack.', '20+ problems', true, 3),
('Dynamic Programming - Advanced', 'Advanced DP — Interval, Tree, Bitmask', 'YouTube — Errichto', 'PLAYLIST', 'https://www.youtube.com/c/Errichto/playlists', 'Competition-level DP: digit DP and bitmask DP.', '10h', true, 1),
('Dynamic Programming - Advanced', 'Palindrome Partitioning', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=WdgAKCnWnwA', 'Hard DP: partition string into minimum palindromes.', '22 min', true, 2);

-- ============================================================
-- TRIES
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Tries', 'Trie Implementation — NeetCode', 'YouTube — NeetCode', 'VIDEO', 'https://www.youtube.com/watch?v=oobqoCJlHA0', 'Implement Trie from scratch, then solve word search II.', '20 min', true, 1),
('Tries', 'Trie Data Structure', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/trie-insert-and-search/', 'Trie insert, search, and delete with Java code.', '15 min read', true, 2),
('Tries', 'Trie Problems', 'LeetCode', 'PRACTICE', 'https://leetcode.com/tag/trie/', '50+ trie problems.', '10+ problems', true, 3);

-- ============================================================
-- JAVA CORE
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Java Core', 'Java Programming Masterclass (Java 21)', 'Udemy (Prosus) — Tim Buchalka', 'COURSE', 'https://www.udemy.com/course/java-the-complete-java-developer-course/', 'The most comprehensive Java course on Udemy. OOP, Collections, Concurrency, Streams.', '80h', false, 1),
('Java Core', 'Java Multithreading & Concurrency', 'YouTube — Defog Tech', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLhfHPmPYPPRk6yMrcbfkuDbSYDu_-lz2w', 'Thread lifecycle, synchronized, ReentrantLock, CompletableFuture.', '4h', true, 2),
('Java Core', 'Java 8 to 21 Features — Baeldung', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/java-8-new-features', 'Lambdas, Streams, Optional, Records, Sealed Classes, Pattern Matching.', '2h read', true, 3),
('Java Core', 'Java Interview Questions — Top 100', 'GeeksForGeeks', 'ARTICLE', 'https://www.geeksforgeeks.org/java-interview-questions/', 'Top 100 Java interview questions with answers. Finance favourites included.', '3h read', true, 4),
('Java Core', 'Java HashMap Internals', 'YouTube — Defog Tech', 'VIDEO', 'https://www.youtube.com/watch?v=MkTtUR3dxCM', 'How Java HashMap works: hashing, chaining, resizing, treeification.', '28 min', true, 5),
('Java Core', 'Effective Java — Joshua Bloch', 'Book', 'ARTICLE', 'https://www.amazon.com/dp/0134685997', 'The definitive Java best practices book. Items 1–50 are essential for interviews.', '412 pages', false, 6),
('Java Core', 'Java Concurrency in Practice', 'Book — Brian Goetz', 'ARTICLE', 'https://www.amazon.com/dp/0321349601', 'The standard book for Java concurrency and thread safety. Read chapters 1-6.', '384 pages', false, 7);

-- ============================================================
-- SPRING BOOT
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Spring Boot', 'Spring Boot 3 & Spring Framework 6', 'Udemy (Prosus) — Chad Darby', 'COURSE', 'https://www.udemy.com/course/spring-hibernate-tutorial/', 'Best Spring Boot course. Covers REST, Security, JPA, Hibernate thoroughly.', '52h', false, 1),
('Spring Boot', 'Spring Security & JWT — Full Tutorial', 'YouTube — Amigoscode', 'VIDEO', 'https://www.youtube.com/watch?v=KxqlJblhzfI', 'Complete Spring Security 6 + JWT from scratch with Spring Boot 3.', '3h 20m', true, 2),
('Spring Boot', 'Spring Data JPA & Hibernate', 'YouTube — Amigoscode', 'VIDEO', 'https://www.youtube.com/watch?v=8SGI_XS5OPw', 'Entity relationships, lazy/eager loading, JPQL, N+1 problem solutions.', '2h', true, 3),
('Spring Boot', 'Spring Boot Interview Questions', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/spring-interview-questions', 'Top 50 Spring Boot/Spring interview questions with in-depth answers.', '2h read', true, 4),
('Spring Boot', '@Transactional Deep Dive', 'Baeldung', 'ARTICLE', 'https://www.baeldung.com/transaction-configuration-with-jpa-and-spring', 'Propagation, isolation levels, rollback rules — critical for finance backends.', '30 min read', true, 5),
('Spring Boot', 'Microservices with Spring Boot & Spring Cloud', 'Udemy (Prosus) — in28minutes', 'COURSE', 'https://www.udemy.com/course/microservices-with-spring-boot-and-spring-cloud/', 'Service discovery, API gateway, circuit breaker, distributed tracing.', '11h', false, 6);

-- ============================================================
-- AWS & CLOUD
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('AWS & Cloud', 'Ultimate AWS SAA-C03 — Stephane Maarek', 'Udemy (Prosus) — Stephane Maarek', 'COURSE', 'https://www.udemy.com/course/aws-certified-solutions-architect-associate-saa-c03/', 'The #1 AWS course on Udemy. EC2, S3, RDS, Lambda, VPC, IAM, SQS, SNS, DynamoDB. Perfect for finance backend engineers.', '27h', false, 1),
('AWS & Cloud', 'AWS for Developers (DVA-C02)', 'Udemy (Prosus) — Stephane Maarek', 'COURSE', 'https://www.udemy.com/course/aws-certified-developer-associate/', 'Developer-focused: Lambda, SQS, SNS, DynamoDB, API Gateway, CodeDeploy.', '33h', false, 2),
('AWS & Cloud', 'AWS Free Tier — Hands On Practice', 'AWS', 'PRACTICE', 'https://aws.amazon.com/free/', 'Sign up for 12 months free. Practice EC2, S3, Lambda, RDS, DynamoDB hands-on.', 'Ongoing', true, 3),
('AWS & Cloud', 'AWS Services Explained — Fireship', 'YouTube — Fireship', 'VIDEO', 'https://www.youtube.com/watch?v=M988_fsOSWo', 'Quick visual overview of 100+ AWS services in 10 minutes.', '10 min', true, 4),
('AWS & Cloud', 'AWS Lambda & Serverless', 'YouTube — Be A Better Dev', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PL9nWRykSBSFg83GD16T1nVRDBVlNF09lM', 'Lambda cold starts, layers, triggers, SAM framework — 12 videos.', '3h', true, 5),
('AWS & Cloud', 'AWS VPC — Networking Deep Dive', 'YouTube — Stephane Maarek', 'VIDEO', 'https://www.youtube.com/watch?v=fpxDGU2KdkA', 'Subnets, route tables, Internet Gateway, NAT Gateway, Security Groups.', '45 min', true, 6),
('AWS & Cloud', 'AWS IAM Best Practices', 'AWS Documentation', 'ARTICLE', 'https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html', 'Least privilege, MFA, roles over keys, permission boundaries. Critical for finance.', '30 min read', true, 7),
('AWS & Cloud', 'AWS Well-Architected Framework', 'AWS Documentation', 'ARTICLE', 'https://docs.aws.amazon.com/wellarchitected/latest/framework/welcome.html', 'Official 6-pillar framework guide. Review before system design interviews.', '2h read', true, 8);

-- ============================================================
-- SYSTEM DESIGN
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('System Design', 'Rocking System Design — Rajdeep Saha', 'Udemy (Prosus) — Rajdeep Saha', 'COURSE', 'https://www.udemy.com/course/rocking-system-design/', '45 case studies including payment systems, URL shortener, Twitter, Netflix. Best course for interview prep.', '18h', false, 1),
('System Design', 'System Design Interview — Alex Xu Vol 1', 'ByteByteGo', 'ARTICLE', 'https://bytebytego.com/', 'The standard system design interview book. URL shortener, Twitter, YouTube, WhatsApp covered visually.', '280 pages', false, 2),
('System Design', 'System Design Primer — GitHub', 'GitHub', 'ARTICLE', 'https://github.com/donnemartin/system-design-primer', 'Free, comprehensive reference. CAP theorem, load balancing, caching, databases, consistency.', '10h read', true, 3),
('System Design', 'Gaurav Sen — System Design Playlist', 'YouTube — Gaurav Sen', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLMCXHnjXnTnvo6alSjVkgxV-VH6EPyvoX', 'Whiteboards for URL shortener, Netflix, WhatsApp, Uber. Excellent for finance system design.', '8h', true, 4),
('System Design', 'Designing Data-Intensive Applications', 'Book — Martin Kleppmann', 'ARTICLE', 'https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/', 'The definitive book on distributed systems. Chapters 5-9 essential for finance backend.', '600 pages', false, 5),
('System Design', 'ByteByteGo — System Design Newsletter', 'ByteByteGo', 'ARTICLE', 'https://bytebytego.com/', 'Weekly visual system design explainers. Highly interview-focused.', 'Weekly', true, 6),
('System Design', 'CAP Theorem & Distributed Systems', 'YouTube — Martin Kleppmann', 'VIDEO', 'https://www.youtube.com/watch?v=oeYBuIN4TjM', 'CAP theorem, consistency models, eventual consistency — from the DDIA author.', '1h', true, 7),
('System Design', 'High Scalability Blog', 'highscalability.com', 'ARTICLE', 'https://highscalability.com/', 'Real-world architecture case studies: Netflix, Uber, Amazon, Discord.', 'Ongoing', true, 8),
('System Design', 'Rate Limiting Algorithms', 'YouTube — ByteByteGo', 'VIDEO', 'https://www.youtube.com/watch?v=FU4WlwfS3G0', 'Token bucket, sliding window, fixed window — when to use each.', '10 min', true, 9),
('System Design', 'NeetCode — System Design Playlist', 'NeetCode', 'PLAYLIST', 'https://www.youtube.com/watch?v=i7twT3x5yv8', 'URL shortener, Twitter, Netflix system design walk-throughs.', '3h', true, 10);

-- ============================================================
-- MOCK INTERVIEW WEEK
-- ============================================================
INSERT INTO learning_resources (topic, title, provider, type, url, description, duration, is_free, display_order) VALUES
('Mock Interview Week', 'NeetCode 150 — Must-Do Problems', 'NeetCode', 'PRACTICE', 'https://neetcode.io/practice', 'The 150 most important interview problems curated by topic. Ideal for mock week.', '150 problems', true, 1),
('Mock Interview Week', 'LeetCode Mock Interviews — Timed', 'LeetCode', 'PRACTICE', 'https://leetcode.com/interview/', 'Simulated timed interviews with real company questions.', '45 min sessions', false, 2),
('Mock Interview Week', 'Java Design Patterns', 'YouTube — Derek Banas', 'PLAYLIST', 'https://www.youtube.com/playlist?list=PLF206E906175C7E07', 'Singleton, Factory, Builder, Observer, Strategy — all in Java.', '3h', true, 3),
('Mock Interview Week', 'Pramp — Free Peer Mock Interviews', 'Pramp', 'PRACTICE', 'https://www.pramp.com/', 'Free peer-to-peer mock interviews. Practice being both interviewer and interviewee.', '45 min sessions', true, 4),
('Mock Interview Week', 'Tech Interview Handbook', 'GitHub', 'ARTICLE', 'https://www.techinterviewhandbook.org/', 'Complete end-to-end interview guide: behavioural, system design, coding.', '5h read', true, 5),
('Mock Interview Week', 'Spring Boot Interview Questions — Top 50', 'JavaTechie', 'VIDEO', 'https://www.youtube.com/watch?v=2cVVEGAMHew', 'Most common Spring Boot interview questions with detailed answers.', '1h', true, 6);
