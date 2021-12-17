#!/usr/bin/env python3

import numpy as np
import copy
import itertools

map0 = """\
2199943210
3987894921
9856789892
8767896789
9899965678"""

def is_low(m, x, y):
    (xsize, ysize) = m.shape
    neighbours = [(x + a, y + b) for (a, b) in [(-1, 0), (1, 0), (0, -1), (0, 1)]]
    neighbours = [(a, b) for (a, b) in neighbours if a >=0 and a < xsize and b >=0 and b < ysize]
    v = m[x][y]
    res = all([m[a][b] > v for (a,b) in neighbours])
    return res

def part0():
    m = np.array([[int(i) for i in line] for line in map0.split('\n')])
    res = 0
    for x in range(m.shape[0]):
        for y in range(m.shape[1]):
            if (is_low(m, x, y)):
                #print(x, y, m[x][y])
                res += m[x][y] + 1
    assert 15 == res

def part1():
    m = np.array([[int(i) for i in line.strip()] for line in open('input9.txt')])
    res = 0
    for x in range(m.shape[0]):
        for y in range(m.shape[1]):
            if (is_low(m, x, y)):
                #print(x, y, m[x][y])
                res += m[x][y] + 1
    assert 423 == res

def basin(m, low_point, seen):
    (x, y) = low_point
    v = m[x][y]
    (xsize, ysize) = m.shape
    neighbours = [(x + a, y + b) for (a, b) in [(-1, 0), (1, 0), (0, -1), (0, 1)]]
    neighbours = [(a, b) for (a, b) in neighbours if a >=0 and a < xsize and b >=0 and b < ysize]
    neighbours = [(a, b) for (a, b) in neighbours if m[a][b] >= v and m[a][b] < 9]
    neighbours = [(a, b) for (a, b) in neighbours if (a,b) not in seen]
    res = neighbours
    for n in neighbours:
        res += basin(m, n, seen + neighbours)
    return res


def part2_0():
    m = np.array([[int(i) for i in line] for line in map0.split('\n')])
    #print(m)
    res = []
    z = []
    for x in range(m.shape[0]):
        for y in range(m.shape[1]):
            if (is_low(m, x, y)):
                #print(x, y, m[x][y])
                res.append((x, y))
    for low_point in res:
        b = [low_point] + basin(m, low_point, [low_point])
        #print(len(b), b)
        z.append(len(b))
    z = sorted(z)
    z.reverse()
    #print(z)
    q = z[0] * z[1] * z[2]
    assert 1134 == q

def part2():
    m = np.array([[int(i) for i in line.strip()] for line in open('input9.txt')])
    print(m)
    res = []
    z = []
    for x in range(m.shape[0]):
        for y in range(m.shape[1]):
            if (is_low(m, x, y)):
                res.append((x, y))
    for low_point in res:
        b = [low_point] + basin(m, low_point, [low_point])
        z.append(len(b))
    z = sorted(z)
    z.reverse()
    q = z[0] * z[1] * z[2]
    print(q)
    assert 1198704 == q

if __name__ == '__main__':
    part0()
    part1()
    part2_0()
    part2()
