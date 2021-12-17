#!/usr/bin/env python3

import numpy as np
import copy
import itertools

input0 = """\
6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
"""


def foldy(m, y):
    print(m.shape)
    for y2 in range(y + 1, m.shape[0]):
        offset = y2 - y
        destination = y - offset
        for x in range(0, m.shape[1]):
            if m[destination][x] == 0:
                m[destination][x] = m[y2][x]
    m = np.delete(m, range(y, m.shape[0]), 0)
    return m

def foldx(m, x):
    print(m.shape)
    for x2 in range(x + 1, m.shape[1]):
        offset = x2 - x
        destination = x - offset
        for y in range(0, m.shape[0]):
            if m[y][destination] == 0:
                m[y][destination] = m[y][x2]
    m = np.delete(m, range(x, m.shape[1]), 1)
    return m

def show_m(m):
    for row in m:
        s = ""
        for a in row:
            if a == 1:
                s += "x"
            else:
                s += " "
        print(s)

def part0():
    coordinates = []
    folds = []
    for line in input0.split('\n'):
        if line.startswith('fold'):
            (f,g) = line.split()[2].split('=')
            folds.append((f, int(g)))
        elif ',' in line:
            (x,y) = line.split(',')
            coordinates.append((int(x), int(y)))
    print(coordinates, folds)
    xdim = max([x for (x,y) in coordinates])+1
    ydim = max([y for (x,y) in coordinates])+1
    print(xdim, ydim)
    m = np.zeros((ydim, xdim))
    for (x,y) in coordinates:
        m[y][x] = 1
    print(m)
    m = foldy(m, 7)
    print(m)
    c = np.count_nonzero(m == 1)
    assert 17 == c
    m = foldx(m, 5)
    print(m)
    print(np.count_nonzero(m == 1))


def part1():
    coordinates = []
    folds = []
    for line in open('input13.txt'):
        if line.startswith('fold'):
            (f,g) = line.split()[2].split('=')
            folds.append((f, int(g)))
        elif ',' in line:
            (x,y) = line.split(',')
            coordinates.append((int(x), int(y)))
    print(folds)
    xdim = max([x for (x,y) in coordinates]) + 1
    ydim = max([y for (x,y) in coordinates]) + 1
    print(xdim, ydim)
    m = np.zeros((ydim, xdim))
    for (x,y) in coordinates:
        m[y][x] = 1
    m = foldx(m, 655)
    c = np.count_nonzero(m == 1)
    print(c)
    assert 708 == c


def part2():
    coordinates = []
    folds = []
    for line in open('input13.txt'):
        if line.startswith('fold'):
            (f,g) = line.split()[2].split('=')
            folds.append((f, int(g)))
        elif ',' in line:
            (x,y) = line.split(',')
            coordinates.append((int(x), int(y)))
    xdim = max([x for (x,y) in coordinates]) + 1
    ydim = max([y for (x,y) in coordinates]) + 1
    print(xdim, ydim)
    m = np.zeros((ydim, xdim))
    for (x,y) in coordinates:
        m[y][x] = 1
    for fold in folds:
        if fold[0]=='x':
            m = foldx(m, fold[1])
        else:
            m = foldy(m, fold[1])
    show_m(m)

if __name__ == '__main__':
    part0()
    part1()
    part2()
