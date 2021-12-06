#!/usr/bin/env python

def increasing((prev, count), x):
    if prev < x:
        return (x, count+1)
    else:
        return (x, count)

def part1():
    numbers = [int(line) for line in open('input1.txt')]
    res = reduce(increasing, numbers, (max(numbers)+1, 0))
    print(res[1])

def part2():
    numbers = [int(line) for line in open('input1.txt')]
    subs = [numbers[i]+numbers[i+1]+numbers[i+2] for i in range(0, len(numbers)-2)]
    res = reduce(increasing, subs, (max(subs)+1, 0))
    print(res[1])

if __name__ == '__main__':
    part1()
    part2()
