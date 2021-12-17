#!/usr/bin/env python

import numpy as np

def initial_map():
    return np.zeros(1000 * 1000).reshape(1000, 1000)

def count(m):
    c = 0
    for yrow in m:
        for y in yrow:
            if y > 1:
                c += 1
    return c

def part1():
    m = initial_map()
    lines = [line for line in open('input5.txt')]
    for line in lines:
        s = line.split()
        p1 = [int(x) for x in s[0].split(',')]
        p2 = [int(x) for x in s[2].split(',')]
        if (p1[0] == p2[0]):
            x = p1[0]
            f1 = min(p1[1], p2[1])
            f2 = max(p1[1], p2[1])
            for y in range(f1, f2+1):
                m[x][y] += 1
        elif (p1[1] == p2[1]):
            y = p1[1]
            f1 = min(p1[0], p2[0])
            f2 = max(p1[0], p2[0])
            for x in range(f1, f2+1):
                m[x][y] += 1

    print(count(m))

def step(v1, v2):
    if v2 > v1:
        return 1
    elif v2 < v1:
        return -1
    else:
        return 0

def part2():
    m = initial_map()
    lines = [line for line in open('input5.txt')]
    for line in lines:
        s = line.split()
        p1 = [int(x) for x in s[0].split(',')]
        p2 = [int(x) for x in s[2].split(',')]
        #print(line, p1, p2)
        xstep = step(p1[0], p2[0])
        ystep = step(p1[1], p2[1])
        nsteps = max(abs(p1[0] - p2[0]), abs(p1[1] - p2[1]))
        x = p1[0]
        y = p1[1]
        for i in range(0, nsteps + 1):
            m[x][y] += 1
            x += xstep
            y += ystep

    print(count(m))

if __name__ == '__main__':
    part1()
    part2()
