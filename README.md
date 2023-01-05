

# Advent of Code 2022

https://adventofcode.com/2022

A beautiful set of interesting tasks accompanied by the merry Christmas story.

My solutions to all tasks are implemented in **Java**, in one file each.

Each class usually contains several tests: test1, and test2 are for examples; answer1 and answer2 are solutions for part 1 and part 2. Everything with asserts to catch a bug after refactorings.

## Interesting sum-ups.

### Day 16.
After several iterations, I managed to solve Part2 in 4 seconds. The main improvements are from early pruning "is this state could possibly better than already accepted best if all remaining valves are magically opened?".

### Day 19.

Day 19 was interesting. After several attempts, I've managed to decrease P2 time from 4 minutes to 0.4 seconds. Though the second test still takes 20s. And again, the most important thing is to implement the best possible pruning. In this case: "can this state be better than the currently chosen best, even with simplified requirements?".

<img src="aoc2022.22.jpeg" width="850" alt=""/>

