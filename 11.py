#!/usr/bin/env python3

import numpy as np
import copy
import itertools

map0 = """\
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526"""

map0_100 = """\
0397666866
0749766918
0053976933
0004297822
0004229892
0053222877
0532222966
9322228966
7922286866
6789998766"""

map00 = """\
11111
19991
19191
19991
11111"""

map00_2 = """\
45654
51115
61116
51115
45654"""

neighbours = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]

def pulse(m):
    (xsize, ysize) = m.shape
    pulses = []
    for x in range(xsize):
        for y in range(ysize):
            m[x][y] += 1
    found = True
    while found:
        found = False
        for x in range(xsize):
            for y in range(ysize):
                if m[x][y] > 9:
                    m[x][y] = 0
                    pulses.append((x,y))
                    found = True
                    nn = [(x + a, y + b) for (a,b) in neighbours if (x+a) >= 0 and (x+a) < xsize and (y+b) >= 0 and (y+b) < ysize]
                    nn = [n for n in nn if n not in pulses]
                    for (a,b) in nn:
                        m[a][b] += 1
    return pulses

def part00():
    m = np.array([[int(c) for c in line] for line in map00.split('\n')])
    #print(m)
    for _ in range(2):
        pulse(m)
        #print(m)
    m2 = np.array([[int(c) for c in line] for line in map00_2.split('\n')])
    assert m2.all() == m.all()

def part0():
    m = np.array([[int(c) for c in line] for line in map0.split('\n')])
    c = 0
    for _ in range(100):
        c += len(pulse(m))
    m100 = np.array([[int(c) for c in line] for line in map0_100.split('\n')])
    assert m100.all() == m.all()
    assert 1656 == c


def part1():
    m = np.array([[int(c) for c in line.strip()] for line in open('input11.txt')])
    c = 0
    for _ in range(100):
        c += len(pulse(m))
    assert 1659 == c

def part2_0():
    m = np.array([[int(c) for c in line] for line in map0.split('\n')])
    c = 0
    while True:
        c += 1
        if len(pulse(m)) == m.shape[0] * m.shape[1]:
            break
    assert 195 == c

def part2():
    m = np.array([[int(c) for c in line.strip()] for line in open('input11.txt')])
    c = 0
    while True:
        c += 1
        if len(pulse(m)) == m.shape[0] * m.shape[1]:
            break
    assert 227 == c

if __name__ == '__main__':
    part00()
    part0()
    part1()
    part2_0()
    part2()
