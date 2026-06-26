-- V8: Seed solution approaches for key Step 3–6 problems
-- Each INSERT uses NOT EXISTS so it is safe to run after a partial seed.

-- ─── Contains Duplicate ───────────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Nested Loops', 'O(n²)', 'O(1)',
'class Solution {
    public boolean containsDuplicate(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) return true;
            }
        }
        return false;
    }
}',
'Compare every pair of elements.',
'Simple O(n²) — check if any two elements are equal.',
FALSE, 1
FROM problems p WHERE p.slug = 'contains-duplicate'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BETTER', 'Sort + Check Adjacent', 'O(n log n)', 'O(1)',
'class Solution {
    public boolean containsDuplicate(int[] nums) {
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) return true;
        }
        return false;
    }
}',
'Sort the array; duplicates will be adjacent.',
'After sorting, any duplicate must sit next to itself.',
FALSE, 2
FROM problems p WHERE p.slug = 'contains-duplicate'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'BETTER');

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'HashSet', 'O(n)', 'O(n)',
'class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (!seen.add(num)) return true;
        }
        return false;
    }
}',
'Use a HashSet for O(1) lookup. If add() returns false the element was already there.',
'HashSet.add() returns false on duplicate — single pass, O(n) time.',
TRUE, 3
FROM problems p WHERE p.slug = 'contains-duplicate'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Best Time to Buy and Sell Stock ─────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Try Every Pair', 'O(n²)', 'O(1)',
'class Solution {
    public int maxProfit(int[] prices) {
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
            }
        }
        return maxProfit;
    }
}',
'Try buying on every day i and selling on every later day j.',
'Brute force — O(n²) comparisons.',
FALSE, 1
FROM problems p WHERE p.slug = 'best-time-to-buy-sell-stock'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Track Min Price', 'O(n)', 'O(1)',
'class Solution {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minPrice);
            }
        }
        return maxProfit;
    }
}',
'Track the minimum price seen so far. For each price, compute profit if sold today.',
'Greedy: always buy at the lowest price seen so far and sell at the current price.',
TRUE, 2
FROM problems p WHERE p.slug = 'best-time-to-buy-sell-stock'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Product of Array Except Self ────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Nested Loop', 'O(n²)', 'O(n)',
'class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            int product = 1;
            for (int j = 0; j < n; j++) {
                if (i != j) product *= nums[j];
            }
            result[i] = product;
        }
        return result;
    }
}',
'For each index, multiply all other elements.',
'O(n²) — straightforward but not efficient.',
FALSE, 1
FROM problems p WHERE p.slug = 'product-of-array-except-self'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Prefix × Suffix Products', 'O(n)', 'O(1)',
'class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        // Left pass: result[i] = product of all elements to the left
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        // Right pass: multiply in the suffix product
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }
        return result;
    }
}',
'First pass builds prefix products. Second pass (right to left) multiplies in suffix products using a running variable.',
'result[i] = (product of all elements left of i) × (product of all elements right of i). Use O(1) extra space by computing suffix on the fly.',
TRUE, 2
FROM problems p WHERE p.slug = 'product-of-array-except-self'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── 3Sum ─────────────────────────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Three Nested Loops', 'O(n³)', 'O(k)',
'class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> result = new HashSet<>();
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        List<Integer> trip = Arrays.asList(nums[i], nums[j], nums[k]);
                        Collections.sort(trip);
                        result.add(trip);
                    }
                }
            }
        }
        return new ArrayList<>(result);
    }
}',
'Try every triplet. Use a Set to avoid duplicates.',
'O(n³) brute force — works but TLEs for large inputs.',
FALSE, 1
FROM problems p WHERE p.slug = '3sum'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Sort + Two Pointers', 'O(n²)', 'O(k)',
'class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip dup
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++; right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }
}',
'Sort the array. Fix one element with index i, then use two pointers (left, right) to find pairs that sum to -nums[i]. Skip duplicates at each level.',
'Sorting lets us use two pointers to find pairs in O(n). Total: O(n²). Duplicate skipping ensures unique triplets.',
TRUE, 2
FROM problems p WHERE p.slug = '3sum'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Merge Intervals ─────────────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Sort + Merge', 'O(n log n)', 'O(n)',
'class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> merged = new ArrayList<>();
        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] =
                    Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }
        return merged.toArray(new int[merged.size()][]);
    }
}',
'Sort intervals by start time. Iterate: if the current interval overlaps with the last merged one, extend it. Otherwise add a new entry.',
'After sorting by start, any overlapping interval must be adjacent. A single pass is sufficient.',
TRUE, 1
FROM problems p WHERE p.slug = 'merge-intervals'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

-- ─── Longest Consecutive Sequence ────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Sort + Linear Scan', 'O(n log n)', 'O(1)',
'class Solution {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;
        Arrays.sort(nums);
        int longest = 1, current = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1] + 1) {
                current++;
                longest = Math.max(longest, current);
            } else if (nums[i] != nums[i - 1]) {
                current = 1;
            }
        }
        return longest;
    }
}',
'Sort the array, then count consecutive streaks.',
'Sorting makes consecutives adjacent. O(n log n) due to sort.',
FALSE, 1
FROM problems p WHERE p.slug = 'longest-consecutive-sequence'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'HashSet', 'O(n)', 'O(n)',
'class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int longest = 0;
        for (int n : set) {
            // Only start counting from the beginning of a sequence
            if (!set.contains(n - 1)) {
                int len = 1;
                while (set.contains(n + len)) len++;
                longest = Math.max(longest, len);
            }
        }
        return longest;
    }
}',
'Add all numbers to a HashSet. For each number that is the start of a sequence (n-1 not in set), count how long the streak runs using O(1) lookups.',
'Each number is visited at most twice (once in outer loop, once in inner while). Total O(n).',
TRUE, 2
FROM problems p WHERE p.slug = 'longest-consecutive-sequence'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Valid Anagram ────────────────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Sort Both Strings', 'O(n log n)', 'O(n)',
'class Solution {
    public boolean isAnagram(String s, String t) {
        char[] sc = s.toCharArray();
        char[] tc = t.toCharArray();
        Arrays.sort(sc);
        Arrays.sort(tc);
        return Arrays.equals(sc, tc);
    }
}',
'Sort both strings. Anagrams produce identical sorted strings.',
'Simple but O(n log n) due to sorting.',
FALSE, 1
FROM problems p WHERE p.slug = 'valid-anagram'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Frequency Count Array', 'O(n)', 'O(1)',
'class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - ''a'']++;
        for (char c : t.toCharArray()) count[c - ''a'']--;
        for (int c : count) if (c != 0) return false;
        return true;
    }
}',
'Count character frequencies in s using a 26-element array. Decrement for each char in t. If all counts are zero, it is an anagram.',
'Alphabet is fixed at 26 → O(1) extra space. Single pass O(n).',
TRUE, 2
FROM problems p WHERE p.slug = 'valid-anagram'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Merge Two Sorted Lists ───────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Collect + Sort', 'O((m+n) log(m+n))', 'O(m+n)',
'class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        List<Integer> vals = new ArrayList<>();
        while (l1 != null) { vals.add(l1.val); l1 = l1.next; }
        while (l2 != null) { vals.add(l2.val); l2 = l2.next; }
        Collections.sort(vals);
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }
}',
'Collect all values, sort them, rebuild the list.',
'Wastes the pre-sorted property of both lists.',
FALSE, 1
FROM problems p WHERE p.slug = 'merge-two-sorted-lists'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Two Pointer Merge', 'O(m+n)', 'O(1)',
'class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}',
'Use a dummy head. Compare front nodes of both lists and link the smaller one. Append the remaining list at the end.',
'Classic two-pointer merge from merge sort. O(m+n) time, O(1) extra space.',
TRUE, 2
FROM problems p WHERE p.slug = 'merge-two-sorted-lists'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Linked List Cycle ────────────────────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'HashSet of Visited Nodes', 'O(n)', 'O(n)',
'class Solution {
    public boolean hasCycle(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        ListNode cur = head;
        while (cur != null) {
            if (visited.contains(cur)) return true;
            visited.add(cur);
            cur = cur.next;
        }
        return false;
    }
}',
'Store each visited node in a HashSet. If a node is visited twice, there is a cycle.',
'O(n) time and space — straightforward but uses extra memory.',
FALSE, 1
FROM problems p WHERE p.slug = 'detect-cycle'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Floyd Cycle Detection', 'O(n)', 'O(1)',
'class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }
}',
'Slow pointer moves one step; fast pointer moves two steps. If there is a cycle, they will eventually meet inside it.',
'Floyd''s tortoise and hare: the fast pointer laps the slow pointer inside the cycle. O(1) extra space.',
TRUE, 2
FROM problems p WHERE p.slug = 'detect-cycle'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');

-- ─── Search in Rotated Sorted Array ──────────────────────────────────────────
INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'BRUTE_FORCE', 'Linear Search', 'O(n)', 'O(1)',
'class Solution {
    public int search(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
        }
        return -1;
    }
}',
'Scan the array linearly.',
'Ignores the sorted+rotated structure entirely.',
FALSE, 1
FROM problems p WHERE p.slug = 'search-rotated-sorted'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id);

INSERT INTO solution_approaches (id, problem_id, approach_type, approach_name, time_complexity, space_complexity, code, explanation, intuition, is_optimal, order_index)
SELECT UUID(), p.id, 'OPTIMAL', 'Modified Binary Search', 'O(log n)', 'O(1)',
'class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            // Left half is sorted
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else { // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }
}',
'At each step, at least one half of the array is sorted. Determine which half the target falls in and discard the other.',
'In a rotated array, at least one half is always sorted. We can binary search by checking which half is sorted and whether the target lies there.',
TRUE, 2
FROM problems p WHERE p.slug = 'search-rotated-sorted'
  AND NOT EXISTS (SELECT 1 FROM solution_approaches sa WHERE sa.problem_id = p.id AND sa.approach_type = 'OPTIMAL');
