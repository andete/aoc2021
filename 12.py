#!/usr/bin/env python3

import numpy as np
import copy
import itertools

v000 = """\
start-A
start-b
A-c
A-b
b-d
A-end
b-end"""

v00 = """\
dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc"""

v0 = """\
fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW"""

def paths(vertices, position, visited, small_visited):
    if position.islower():
        small_visited.append(position)
    visited.append(position)
    if position == 'end':
        return [[position]]
    next = [b for (a,b) in vertices if a == position and b not in small_visited]
    #print(position, next)
    res = []
    for n in next:
        for p in paths(vertices, n, visited.copy(), small_visited.copy()):
            res.append([position] + p)
    #print('paths', position, 'to end:', res)
    return res

def paths2(vertices, position, visited, small_visited, small_visited_twice):
    res = []
    visited.append(position)
    if position == 'end':
        return [[position]]
    next = [b for (a,b) in vertices if a == position and b not in small_visited]
    next2 = []
    if small_visited_twice == None:
        next2 = [b for (a,b) in vertices if a == position and b in small_visited and b != 'start']
    if position.islower():
        small_visited.append(position)
    res = []
    for n in next:
        for p in paths2(vertices, n, visited.copy(), small_visited.copy(), small_visited_twice):
            res.append([position] + p)
    for n in next2:
        for p in paths2(vertices, n, visited.copy(), small_visited.copy(), n):
            res.append([position] + p)
    #print('paths', position, 'to end:', res)
    return res


def part000():
    vertices = [x.split('-') for x in v000.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths(vertices, 'start', [], [])
    print(len(p), p)
    assert 10 == len(p)

def part00():
    vertices = [x.split('-') for x in v00.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths(vertices, 'start', [], [])
    print(len(p), p)
    assert 19 == len(p)

def part0():
    vertices = [x.split('-') for x in v0.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths(vertices, 'start', [], [])
    print(len(p))
    assert 226 == len(p)

def part1():
    vertices = [x.strip().split('-') for x in open('input12.txt')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths(vertices, 'start', [], [])
    print(len(p))
    assert 4720 == len(p)

def part2_000():
    vertices = [x.split('-') for x in v000.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths2(vertices, 'start', [], [], None)
    print(len(p), p)
    assert 36 == len(p)

def part2_00():
    vertices = [x.split('-') for x in v00.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths2(vertices, 'start', [], [], None)
    print(len(p), p)
    assert 103 == len(p)

def part2_0():
    vertices = [x.split('-') for x in v0.split('\n')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths2(vertices, 'start', [], [], None)
    print(len(p), p)
    assert 3509 == len(p)

def part2():
    vertices = [x.strip().split('-') for x in open('input12.txt')]
    vertices = [(b,a) for (a,b) in vertices] + vertices
    #print('lower:', [x for x in set([a for (a,b) in vertices]) if x.islower()])
    print(vertices)
    p = paths2(vertices, 'start', [], [], None)
    print(len(p))
    assert 147848 == len(p)

if __name__ == '__main__':
    part000()
    part00()
    part0()
    part1()
    part2_000()
    part2_00()
    part2_0()
    part2()
