-- ============================================================
-- V7: A2Z Problems - Step 1: Learn the Basics (MySQL Version)
-- ============================================================

-- Clear existing seed data
DELETE FROM solution_approaches;
DELETE FROM problems WHERE topic IN ('Basics', 'Sorting', 'Arrays', 'Binary Search', 'Strings', 'Linked List');

-- Step 1: Learn the Basics - User Input/Output (8 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'User Input and Output', 'user-input-output', 'EASY', 'Basics', 1, 'Learn the Basics', 'User Input/Output', '["Basics"]', 1, 'https://www.youtube.com/watch?v=EURaF8NX8KI', 'https://takeuforward.org/c-programming/basic-input-output-c/', 'Write a program to take user input and print it back.', 'Basic input output operations.', '[{"input": "Hello", "output": "Hello"}]', '["Use Scanner in Java", "Use cin in C++"]', TRUE, NOW()),
(UUID(), 'Data Types', 'data-types', 'EASY', 'Basics', 1, 'Learn the Basics', 'Data Types', '["Basics"]', 2, 'https://www.youtube.com/watch?v=EURaF8NX8KI', 'https://takeuforward.org/c-programming/data-types-in-c/', 'Understand and implement different data types.', 'All primitive data types.', '[{"input": "5", "output": "Integer: 5"}]', '["Learn int, long, float, double, char, boolean"]', TRUE, NOW()),
(UUID(), 'If Else Statements', 'if-else-statements', 'EASY', 'Basics', 1, 'Learn the Basics', 'If Else', '["Basics"]', 3, 'https://www.youtube.com/watch?v=EURaF8NX8KI', 'https://takeuforward.org/c-programming/if-else-statements-c/', 'Implement conditional logic using if-else.', 'Basic condition checking.', '[{"input": "age = 20", "output": "Adult"}]', '["Use comparison operators", "Check boundary conditions"]', TRUE, NOW()),
(UUID(), 'For Loops', 'for-loops', 'EASY', 'Basics', 1, 'Learn the Basics', 'For Loops', '["Basics"]', 4, 'https://www.youtube.com/watch?v=EURaF8NX8KI', 'https://takeuforward.org/c-programming/loops-in-c/', 'Master for loop iterations.', 'Basic iteration patterns.', '[{"input": "n=5", "output": "1 2 3 4 5"}]', '["Initialize, condition, increment", "Practice pattern printing"]', TRUE, NOW()),
(UUID(), 'While Loops', 'while-loops', 'EASY', 'Basics', 1, 'Learn the Basics', 'While Loops', '["Basics"]', 5, 'https://www.youtube.com/watch?v=EURaF8NX8KI', 'https://takeuforward.org/c-programming/loops-in-c/', 'Master while loop iterations.', 'Condition-based iteration.', '[{"input": "n=5", "output": "5 4 3 2 1"}]', '["Check condition before iteration", "Update condition variable"]', TRUE, NOW());

-- Step 1: Pattern Problems (5 key patterns)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Pattern 1: Rectangular Star Pattern', 'pattern-rectangular-star', 'EASY', 'Basics', 1, 'Learn the Basics', 'Patterns', '["Pattern Printing"]', 6, 'https://www.youtube.com/watch?v=tNm_NNSB3_w', 'https://takeuforward.org/strivers-a2z-dsa-course/mastery-a2z-dsa-sheet/', 'Print a rectangular pattern of stars.', 'n x m rectangle of stars.', '[{"input": "n=3, m=3", "output": "* * *\\n* * *\\n* * *"}]', '["Use nested loops", "Outer for rows, inner for columns"]', TRUE, NOW()),
(UUID(), 'Pattern 2: Right-Angled Triangle', 'pattern-right-triangle', 'EASY', 'Basics', 1, 'Learn the Basics', 'Patterns', '["Pattern Printing"]', 7, 'https://www.youtube.com/watch?v=tNm_NNSB3_w', 'https://takeuforward.org/strivers-a2z-dsa-course/mastery-a2z-dsa-sheet/', 'Print a right-angled triangle of stars.', 'Row i has i stars.', '[{"input": "n=5", "output": "*\\n* *\\n* * *\\n* * * *\\n* * * * *"}]', '["Inner loop runs from 1 to i", "Print star and space"]', TRUE, NOW()),
(UUID(), 'Pattern 8: Diamond Star Pattern', 'pattern-diamond', 'EASY', 'Basics', 1, 'Learn the Basics', 'Patterns', '["Pattern Printing"]', 8, 'https://www.youtube.com/watch?v=tNm_NNSB3_w', 'https://takeuforward.org/strivers-a2z-dsa-course/mastery-a2z-dsa-sheet/', 'Print diamond pattern.', 'Combine pyramid and inverted pyramid.', '[{"input": "n=3", "output": "  *\\n ***\\n*****\\n*****\\n ***\\n  *"}]', '["First print upper half", "Then print lower half"]', TRUE, NOW());

-- Step 1: Basic Maths (5 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Count Digits', 'count-digits', 'EASY', 'Basics', 1, 'Learn the Basics', 'Basic Maths', '["Math"]', 9, 'https://www.youtube.com/watch?v=1xNbjMdbjug', 'https://takeuforward.org/data-structure/count-digits-in-a-number/', 'Given a number n, count the number of digits.', '0 <= n <= 10^9', '[{"input": "n = 12345", "output": "5"}]', '["Use log10 for optimal", "Or keep dividing by 10"]', TRUE, NOW()),
(UUID(), 'Reverse a Number', 'reverse-number', 'EASY', 'Basics', 1, 'Learn the Basics', 'Basic Maths', '["Math"]', 10, 'https://www.youtube.com/watch?v=1xNbjMdbjug', 'https://takeuforward.org/data-structure/reverse-a-number/', 'Reverse the digits of a number.', '-2^31 <= n <= 2^31 - 1', '[{"input": "n = 123", "output": "321"}]', '["Handle negative numbers", "Check for overflow"]', TRUE, NOW()),
(UUID(), 'GCD or HCF', 'gcd-hcf', 'EASY', 'Basics', 1, 'Learn the Basics', 'Basic Maths', '["Math", "GCD"]', 11, 'https://www.youtube.com/watch?v=1xNbjMdbjug', 'https://takeuforward.org/data-structure/find-gcd-of-two-numbers/', 'Find GCD of two numbers.', '1 <= a, b <= 10^9', '[{"input": "a=9, b=6", "output": "3"}]', '["Use Euclidean algorithm", "GCD(a,b) = GCD(b, a%b)"]', TRUE, NOW()),
(UUID(), 'Check for Prime', 'check-for-prime', 'EASY', 'Basics', 1, 'Learn the Basics', 'Basic Maths', '["Math", "Prime"]', 12, 'https://www.youtube.com/watch?v=1xNbjMdbjug', 'https://takeuforward.org/data-structure/check-if-a-number-is-prime-or-not/', 'Check if a number is prime.', '1 <= n <= 10^9', '[{"input": "n = 7", "output": "true"}]', '["Check divisibility up to sqrt(n)", "Handle n=1 as not prime"]', TRUE, NOW());

-- Step 1: Basic Recursion (3 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Print 1 to N using Recursion', 'print-1-to-n-recursion', 'EASY', 'Basics', 1, 'Learn the Basics', 'Recursion Basics', '["Recursion"]', 13, 'https://www.youtube.com/watch?v=JxILx9hd3', 'https://takeuforward.org/data-structure/print-1-to-n-using-recursion/', 'Print numbers from 1 to n using recursion.', '1 <= n <= 1000', '[{"input": "n=5", "output": "1 2 3 4 5"}]', '["Base case: n=0 return", "Recursive call before or after printing"]', TRUE, NOW()),
(UUID(), 'Factorial of N', 'factorial-of-n', 'EASY', 'Basics', 1, 'Learn the Basics', 'Recursion Basics', '["Recursion"]', 14, 'https://www.youtube.com/watch?v=JxILx9hd3', 'https://takeuforward.org/data-structure/factorial-of-a-number/', 'Find factorial of n using recursion.', '0 <= n <= 20', '[{"input": "n=5", "output": "120"}]', '["return n * fact(n-1)", "Base case: n=0 or n=1 return 1"]', TRUE, NOW()),
(UUID(), 'Fibonacci Number', 'fibonacci-number', 'EASY', 'Basics', 1, 'Learn the Basics', 'Recursion Basics', '["Recursion", "DP"]', 15, 'https://www.youtube.com/watch?v=JxILx9hd3', 'https://takeuforward.org/data-structure/print-fibonacci-series-up-to-nth-term/', 'Find nth Fibonacci number using recursion.', '0 <= n <= 30', '[{"input": "n=6", "output": "8"}]', '["Base cases: F(0)=0, F(1)=1", "Return F(n-1) + F(n-2)"]', TRUE, NOW());

-- Step 2: Sorting Techniques (5 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Selection Sort', 'selection-sort', 'EASY', 'Sorting', 2, 'Sorting Techniques', 'Basic Sorting', '["Sorting", "Selection Sort"]', 1, 'https://www.youtube.com/watch?v=HGk_ypF24N8', 'https://takeuforward.org/sorting/selection-sort-algorithm/', 'Sort array using Selection Sort.', '1 <= n <= 10^5', '[{"input": "[64,25,12,22,11]", "output": "[11,12,22,25,64]"}]', '["Find minimum element and swap with first", "Repeat for remaining array"]', TRUE, NOW()),
(UUID(), 'Bubble Sort', 'bubble-sort', 'EASY', 'Sorting', 2, 'Sorting Techniques', 'Basic Sorting', '["Sorting", "Bubble Sort"]', 2, 'https://www.youtube.com/watch?v=HGk_ypF24N8', 'https://takeuforward.org/sorting/bubble-sort-algorithm/', 'Sort array using Bubble Sort.', '1 <= n <= 10^5', '[{"input": "[5,1,4,2,8]", "output": "[1,2,4,5,8]"}]', '["Adjacent swaps", "Largest bubbles to end"]', TRUE, NOW()),
(UUID(), 'Insertion Sort', 'insertion-sort', 'EASY', 'Sorting', 2, 'Sorting Techniques', 'Basic Sorting', '["Sorting", "Insertion Sort"]', 3, 'https://www.youtube.com/watch?v=HGk_ypF24N8', 'https://takeuforward.org/sorting/insertion-sort-algorithm/', 'Sort array using Insertion Sort.', '1 <= n <= 10^5', '[{"input": "[12,11,13,5,6]", "output": "[5,6,11,12,13]"}]', '["Insert element in sorted portion", "Shift elements greater than key"]', TRUE, NOW()),
(UUID(), 'Merge Sort', 'merge-sort', 'MEDIUM', 'Sorting', 2, 'Sorting Techniques', 'Advanced Sorting', '["Sorting", "Merge Sort", "Divide and Conquer"]', 4, 'https://www.youtube.com/watch?v=ogjf7Ytfdmg', 'https://takeuforward.org/sorting/merge-sort-algorithm/', 'Sort array using Merge Sort.', '1 <= n <= 10^5', '[{"input": "[38,27,43,3,9,82,10]", "output": "[3,9,10,27,38,43,82]"}]', '["Divide array into two halves", "Recursively sort both halves", "Merge the sorted halves"]', TRUE, NOW()),
(UUID(), 'Quick Sort', 'quick-sort', 'MEDIUM', 'Sorting', 2, 'Sorting Techniques', 'Advanced Sorting', '["Sorting", "Quick Sort", "Divide and Conquer"]', 5, 'https://www.youtube.com/watch?v=pg8k6GMjJ4U', 'https://takeuforward.org/sorting/quick-sort-algorithm/', 'Sort array using Quick Sort.', '1 <= n <= 10^5', '[{"input": "[10,7,8,9,1,5]", "output": "[1,5,7,8,9,10]"}]', '["Choose pivot (usually last element)", "Partition array around pivot", "Recursively sort partitions"]', TRUE, NOW());

-- Step 3: Arrays (8 problems with A2Z fields)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Two Sum', 'two-sum', 'EASY', 'Arrays', 3, 'Arrays', 'Easy', '["HashMap", "Two Pointers", "Array"]', 1, 'https://www.youtube.com/watch?v=UXDSeD9mNNo', 'https://takeuforward.org/data-structure/two-sum-check-if-a-pair-with-given-sum-exists-in-array/', 'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.', '2 <= nums.length <= 10^4', '[{"input": "nums = [2,7,11,15], target = 9", "output": "[0,1]", "explanation": "nums[0] + nums[1] == 9"}]', '["Use a HashMap to store complement values", "For each element, check if its complement exists in the map", "Return indices when found"]', TRUE, NOW()),
(UUID(), 'Best Time to Buy and Sell Stock', 'best-time-to-buy-sell-stock', 'EASY', 'Arrays', 3, 'Arrays', 'Easy', '["Greedy", "Kadane", "Array"]', 2, 'https://www.youtube.com/watch?v=34WE6ayA8rY', 'https://takeuforward.org/data-structure/stock-buy-and-sell/', 'Find maximum profit from buying and selling stock once.', '1 <= prices.length <= 10^5', '[{"input": "prices = [7,1,5,3,6,4]", "output": "5", "explanation": "Buy on day 2 (price=1), sell on day 5 (price=6)"}]', '["Track minimum price seen so far", "Calculate profit if sold today", "Update max profit"]', TRUE, NOW()),
(UUID(), 'Contains Duplicate', 'contains-duplicate', 'EASY', 'Arrays', 3, 'Arrays', 'Easy', '["HashSet", "Sorting", "Array"]', 3, 'https://www.youtube.com/watch?v=Dv2hNAe_4JE', 'https://takeuforward.org/data-structure/contains-duplicate/', 'Return true if any value appears at least twice.', '1 <= nums.length <= 10^5', '[{"input": "[1,2,3,1]", "output": "true"}]', '["Try nested loops - O(n²)", "Sorting brings duplicates together", "HashSet stores unique values"]', TRUE, NOW()),
(UUID(), 'Maximum Subarray - Kadane Algorithm', 'maximum-subarray', 'MEDIUM', 'Arrays', 3, 'Arrays', 'Medium', '["Array", "Kadane", "DP"]', 4, 'https://www.youtube.com/watch?v=w_KEocd20T4', 'https://takeuforward.org/data-structure/kadanes-algorithm-maximum-subarray-sum-in-an-array/', 'Find maximum sum of contiguous subarray.', '-10^4 <= nums[i] <= 10^4, 1 <= n <= 10^5', '[{"input": "[-2,1,-3,4,-1,2,1,-5,4]", "output": "6", "explanation": "[4,-1,2,1]"}]', '["Kadane: maxEndingHere = max(nums[i], maxEndingHere+nums[i])", "Track global max"]', TRUE, NOW()),
(UUID(), 'Product of Array Except Self', 'product-of-array-except-self', 'MEDIUM', 'Arrays', 3, 'Arrays', 'Medium', '["Array", "Prefix Product"]', 5, 'https://www.youtube.com/watch?v=vyQRy_xI-1w', 'https://takeuforward.org/data-structure/product-of-array-except-itself/', 'Return array where each element is product of all except self.', '2 <= n <= 10^5', '[{"input": "[1,2,3,4]", "output": "[24,12,8,6]"}]', '["Use prefix and suffix products", "O(n) time, O(1) extra space"]', TRUE, NOW()),
(UUID(), '3Sum', '3sum', 'MEDIUM', 'Arrays', 3, 'Arrays', 'Hard', '["Array", "Two Pointers", "Sorting"]', 6, 'https://www.youtube.com/watch?v=onLoX6Nhvmg', 'https://takeuforward.org/data-structure/3-sum-find-triplets-that-add-up-to-a-zero/', 'Find all unique triplets that sum to 0.', '0 <= n <= 3000', '[{"input": "[-1,0,1,2,-1,-4]", "output": "[[-1,-1,2],[-1,0,1]]"}]', '["Sort first", "Fix one and use two pointers for rest", "Skip duplicates"]', TRUE, NOW()),
(UUID(), 'Merge Intervals', 'merge-intervals', 'MEDIUM', 'Arrays', 3, 'Arrays', 'Medium', '["Array", "Sorting"]', 7, 'https://www.youtube.com/watch?v=IexN60k62qo', 'https://takeuforward.org/data-structure/merge-overlapping-sub-intervals/', 'Merge all overlapping intervals.', '1 <= n <= 10^4', '[{"input": "[[1,3],[2,6],[8,10],[15,18]]", "output": "[[1,6],[8,10],[15,18]]"}]', '["Sort by start time", "Merge overlapping: end = max(end1, end2)", "Add non-overlapping"]', TRUE, NOW()),
(UUID(), 'Longest Consecutive Sequence', 'longest-consecutive-sequence', 'MEDIUM', 'Arrays', 3, 'Arrays', 'Medium', '["Array", "HashSet"]', 8, 'https://www.youtube.com/watch?v=oYfVZBoc1ao', 'https://takeuforward.org/data-structure/longest-consecutive-sequence-in-an-array/', 'Find length of longest consecutive elements sequence.', '0 <= n <= 10^5', '[{"input": "[100,4,200,1,3,2]", "output": "4", "explanation": "[1,2,3,4]"}]', '["Use HashSet", "For each num, if num-1 not in set, start sequence", "Count consecutive"]', TRUE, NOW());

-- Step 4: Binary Search (8 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Binary Search', 'binary-search', 'EASY', 'Binary Search', 4, 'Binary Search', '1D BS', '["Binary Search", "Array"]', 1, 'https://www.youtube.com/watch?v=NXD7b3RqVjQ', 'https://takeuforward.org/data-structure/binary-search-explained/', 'Search target in sorted array with O(log n) complexity.', '1 <= n <= 10^4', '[{"input": "nums = [-1,0,3,5,9,12], target = 9", "output": "4"}]', '["Linear search works but is O(n)", "Array is sorted - can we use this property?", "Divide search space in half each time"]', TRUE, NOW()),
(UUID(), 'Lower Bound', 'lower-bound', 'EASY', 'Binary Search', 4, 'Binary Search', '1D BS', '["Binary Search"]', 2, 'https://www.youtube.com/watch?v=6PrIKdPhCZw', 'https://takeuforward.org/arrays/lower-bound-in-a-sorted-array/', 'Find lower bound of target in sorted array.', '1 <= n <= 10^5', '[{"input": "[1,2,2,3,4,5], target=2", "output": "1"}]', '["First occurrence >= target", "Binary search with <= condition"]', TRUE, NOW()),
(UUID(), 'Search in Rotated Sorted Array', 'search-rotated-sorted', 'MEDIUM', 'Binary Search', 4, 'Binary Search', 'Rotated Sorted', '["Binary Search"]', 3, 'https://www.youtube.com/watch?v=r3pMQ8-Ad5s', 'https://takeuforward.org/arrays/search-element-in-rotated-sorted-array/', 'Search target in rotated sorted array.', '1 <= n <= 5000', '[{"input": "[4,5,6,7,0,1,2], target=0", "output": "4"}]', '["Find which half is sorted", "Check if target in sorted half", "Eliminate other half"]', TRUE, NOW()),
(UUID(), 'Find Peak Element', 'find-peak-element', 'MEDIUM', 'Binary Search', 4, 'Binary Search', '1D BS', '["Binary Search"]', 4, 'https://www.youtube.com/watch?v=r3pMQ8-Ad5s', 'https://takeuforward.org/arrays/find-peak-element/', 'Find index of any peak element.', '1 <= n <= 1000', '[{"input": "[1,2,3,1]", "output": "2", "explanation": "Peak is 3 at index 2"}]', '["Peak if greater than neighbors", "Move towards increasing side", "Binary search based on mid neighbors"]', TRUE, NOW()),
(UUID(), 'Sqrt(x)', 'sqrt-x', 'EASY', 'Binary Search', 4, 'Binary Search', 'Search Space', '["Binary Search"]', 5, 'https://www.youtube.com/watch?v=Be7FhkvO63c', 'https://takeuforward.org/binary-search/floor-in-a-sorted-array/', 'Compute and return square root of x.', '0 <= x <= 2^31 - 1', '[{"input": "x=8", "output": "2", "explanation": "sqrt(8) = 2.828, truncate to 2"}]', '["Binary search on answer", "Find largest mid where mid*mid <= x", "Watch for overflow"]', TRUE, NOW()),
(UUID(), 'Koko Eating Bananas', 'koko-eating-bananas', 'MEDIUM', 'Binary Search', 4, 'Binary Search', 'Search Space', '["Binary Search"]', 6, 'https://www.youtube.com/watch?v=qyfekrNni90', 'https://takeuforward.org/binary-search/koko-eating-bananas/', 'Find minimum eating speed to eat all bananas in h hours.', '1 <= piles.length <= 10^4', '[{"input": "piles=[3,6,7,11], h=8", "output": "4"}]', '["Binary search on speed k", "Calculate hours needed for speed k", "Find minimum k where hours <= h"]', TRUE, NOW()),
(UUID(), 'Capacity to Ship Packages', 'capacity-ship-packages', 'MEDIUM', 'Binary Search', 4, 'Binary Search', 'Search Space', '["Binary Search"]', 7, 'https://www.youtube.com/watch?v=MG-Ac4TAvNk', 'https://takeuforward.org/arrays/capacity-to-ship-packages-within-d-days/', 'Find minimum ship capacity to ship all packages in D days.', '1 <= weights.length <= 5*10^4', '[{"input": "weights=[1,2,3,4,5,6,7,8,9,10], days=5", "output": "15"}]', '["Binary search on capacity", "Lower bound: max weight, Upper bound: sum of weights", "Check if can ship in D days with capacity"]', TRUE, NOW());

-- Step 5: Strings (6 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Reverse Words in String', 'reverse-words-string', 'MEDIUM', 'Strings', 5, 'Strings', 'Medium', '["String", "Two Pointers"]', 1, 'https://www.youtube.com/watch?v=7yyBNIHIpzA', 'https://takeuforward.org/strings/reverse-words-in-a-string/', 'Reverse order of words in string.', '1 <= s.length <= 10^4', '[{"input": "the sky is blue", "output": "blue is sky the"}]', '["Trim spaces", "Reverse entire string", "Reverse each word"]', TRUE, NOW()),
(UUID(), 'Longest Palindromic Substring', 'longest-palindromic-substring', 'MEDIUM', 'Strings', 5, 'Strings', 'Medium', '["String", "Expand Around Center"]', 2, 'https://www.youtube.com/watch?v=UflHuBjU-sU', 'https://takeuforward.org/data-structure/longest-palindromic-substring/', 'Return longest palindromic substring.', '1 <= s.length <= 1000', '[{"input": "babad", "output": "bab or aba"}]', '["Expand around center", "Check both odd and even length palindromes", "Track max length"]', TRUE, NOW()),
(UUID(), 'Roman to Integer', 'roman-to-integer', 'EASY', 'Strings', 5, 'Strings', 'Easy', '["String", "HashMap"]', 3, 'https://www.youtube.com/watch?v=dlfH1z5pDkI', 'https://takeuforward.org/strings/roman-to-integer/', 'Convert Roman numeral to integer.', '1 <= s.length <= 15', '[{"input": "III", "output": "3"}]', '["Use HashMap for values", "If current < next, subtract current", "Else add current"]', TRUE, NOW()),
(UUID(), 'Valid Palindrome', 'valid-palindrome', 'EASY', 'Strings', 5, 'Strings', 'Easy', '["String", "Two Pointers"]', 4, 'https://www.youtube.com/watch?v=dR5JwsnHPro', 'https://takeuforward.org/strings/valid-palindrome/', 'Check if string is palindrome after removing non-alphanumeric chars.', '1 <= s.length <= 2*10^5', '[{"input": "A man, a plan, a canal: Panama", "output": "true"}]', '["Use two pointers", "Skip non-alphanumeric characters", "Compare lowercase versions"]', TRUE, NOW()),
(UUID(), 'Valid Anagram', 'valid-anagram', 'EASY', 'Strings', 5, 'Strings', 'Easy', '["String", "HashMap", "Sorting"]', 5, 'https://www.youtube.com/watch?v=9UtInBqnCgA', 'https://takeuforward.org/strings/valid-anagram/', 'Check if t is anagram of s.', '1 <= s.length, t.length <= 5*10^4', '[{"input": "s=anagram, t=nagaram", "output": "true"}]', '["Sort and compare", "Or use frequency map", "All characters must have same count"]', TRUE, NOW()),
(UUID(), 'Group Anagrams', 'group-anagrams', 'MEDIUM', 'Strings', 5, 'Strings', 'Medium', '["String", "HashMap", "Sorting"]', 6, 'https://www.youtube.com/watch?v=vzdNOK2Jr3g', 'https://takeuforward.org/strings/group-anagrams/', 'Group strings that are anagrams together.', '1 <= strs.length <= 10^4', '[{"input": "[eat,tea,tan,ate,nat,bat]", "output": "[[bat],[nat,tan],[ate,eat,tea]]"}]', '["Sort each string as key in map", "Or use character count as key", "Group by sorted version"]', TRUE, NOW());

-- Step 6: Linked List (7 problems)
INSERT INTO problems (id, title, slug, difficulty, topic, step_number, section_name, sub_topic, pattern_tags, step_order, video_solution_url, article_solution_url, description, constraints_text, examples, hints, is_active, created_at)
VALUES 
(UUID(), 'Reverse Linked List', 'reverse-linked-list', 'EASY', 'Linked List', 6, 'Linked List', 'Easy', '["Linked List", "Reversal", "Iterative", "Recursive"]', 1, 'https://www.youtube.com/watch?v=iRtLEoL-r-g', 'https://takeuforward.org/data-structure/reverse-a-linked-list/', 'Reverse a singly linked list.', '0 <= n <= 5000', '[{"input": "[1,2,3,4,5]", "output": "[5,4,3,2,1]"}]', '["Think about changing next pointers", "Track previous node while iterating", "Recursive: reverse rest, then connect"]', TRUE, NOW()),
(UUID(), 'Middle of Linked List', 'middle-linked-list', 'EASY', 'Linked List', 6, 'Linked List', 'Easy', '["Linked List", "Slow Fast Pointers"]', 2, 'https://www.youtube.com/watch?v=A2iZ_Hg0G7M', 'https://takeuforward.org/data-structure/find-middle-element-in-a-linked-list/', 'Return middle node of linked list.', '1 <= n <= 100', '[{"input": "[1,2,3,4,5]", "output": "[3,4,5]"}]', '["Slow and fast pointer", "Fast moves 2, slow moves 1", "When fast reaches end, slow is at middle"]', TRUE, NOW()),
(UUID(), 'Merge Two Sorted Lists', 'merge-two-sorted-lists', 'EASY', 'Linked List', 6, 'Linked List', 'Easy', '["Linked List", "Two Pointers"]', 3, 'https://www.youtube.com/watch?v=Xb4slcp1U38', 'https://takeuforward.org/data-structure/merge-two-sorted-linked-lists/', 'Merge two sorted linked lists.', '0 <= list1.length, list2.length <= 50', '[{"input": "[1,2,4], [1,3,4]", "output": "[1,1,2,3,4,4]"}]', '["Use dummy node", "Compare and append smaller", "Append remaining nodes"]', TRUE, NOW()),
(UUID(), 'Remove Nth Node From End', 'remove-nth-node-end', 'MEDIUM', 'Linked List', 6, 'Linked List', 'Medium', '["Linked List", "Slow Fast Pointers"]', 4, 'https://www.youtube.com/watch?v=5Bp6GqUJ4nI', 'https://takeuforward.org/data-structure/remove-n-th-node-from-the-end-of-a-linked-list/', 'Remove nth node from end of list.', '1 <= n <= sz <= 30', '[{"input": "[1,2,3,4,5], n=2", "output": "[1,2,3,5]"}]', '["Fast pointer n nodes ahead", "Move both until fast at end", "Remove slow.next"]', TRUE, NOW()),
(UUID(), 'Add Two Numbers', 'add-two-numbers', 'MEDIUM', 'Linked List', 6, 'Linked List', 'Medium', '["Linked List", "Math"]', 5, 'https://www.youtube.com/watch?v=LBVsXSMOIk4', 'https://takeuforward.org/data-structure/add-two-numbers-represented-as-linked-lists/', 'Add two numbers represented by linked lists.', '1 <= l1.length, l2.length <= 100', '[{"input": "[2,4,3], [5,6,4]", "output": "[7,0,8]", "explanation": "342 + 465 = 807"}]', '["Traverse both lists", "Add digits with carry", "Create new node for sum%10"]', TRUE, NOW()),
(UUID(), 'Detect Cycle', 'detect-cycle', 'EASY', 'Linked List', 6, 'Linked List', 'Easy', '["Linked List", "Slow Fast Pointers"]', 6, 'https://www.youtube.com/watch?v=354J83hX7i0', 'https://takeuforward.org/data-structure/detect-a-cycle-in-a-linked-list/', 'Return node where cycle begins.', '0 <= n <= 10^4', '[{"input": "[3,2,0,-4], pos=1", "output": "Node with index 1"}]', '["Slow and fast pointer", "If they meet, cycle exists", "Move one to head, both move 1 step to find start"]', TRUE, NOW()),
(UUID(), 'Reorder List', 'reorder-list', 'MEDIUM', 'Linked List', 6, 'Linked List', 'Medium', '["Linked List", "Slow Fast Pointers", "Reversal"]', 7, 'https://www.youtube.com/watch?v=S5bfdUTrKLM', 'https://takeuforward.org/data-structure/reorder-a-linked-list/', 'Reorder list: L0->Ln->L1->Ln-1->...', '1 <= n <= 5*10^4', '[{"input": "[1,2,3,4]", "output": "[1,4,2,3]"}]', '["Find middle", "Reverse second half", "Merge alternating from both halves"]', TRUE, NOW());

-- Add solution approaches for key problems
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT 
    UUID(),
    p.id,
    'BRUTE_FORCE',
    'Nested Loops',
    'O(n²)',
    'O(1)',
    'class Solution {
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }
}',
    'Check every possible pair of elements.',
    'Simple brute force - compare every element with every other element.',
    FALSE,
    1
FROM problems p WHERE p.slug = 'two-sum';

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT 
    UUID(),
    p.id,
    'OPTIMAL',
    'HashMap',
    'O(n)',
    'O(n)',
    'class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}',
    'Use a HashMap to store values and their indices.',
    'HashMap gives O(1) lookups. Single pass = O(n) time.',
    TRUE,
    2
FROM problems p WHERE p.slug = 'two-sum';

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT 
    UUID(),
    p.id,
    'OPTIMAL',
    'Kadane Algorithm',
    'O(n)',
    'O(1)',
    'class Solution {
    public int maxSubArray(int[] nums) {
        int maxEndingHere = nums[0];
        int maxSoFar = nums[0];
        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }
}',
    'DP approach: track max subarray ending at each position.',
    'If running sum becomes negative, start fresh from next element.',
    TRUE,
    1
FROM problems p WHERE p.slug = 'maximum-subarray';

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT 
    UUID(),
    p.id,
    'OPTIMAL',
    'Iterative - Three Pointers',
    'O(n)',
    'O(1)',
    'class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        return prev;
    }
}',
    'Change next pointers to point backwards.',
    'Reverse the direction of links using three pointers.',
    TRUE,
    1
FROM problems p WHERE p.slug = 'reverse-linked-list';

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT 
    UUID(),
    p.id,
    'OPTIMAL',
    'Binary Search',
    'O(log n)',
    'O(1)',
    'class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }
}',
    'Divide search space in half at each step.',
    'Since array is sorted, we can eliminate half the elements.',
    TRUE,
    1
FROM problems p WHERE p.slug = 'binary-search';
