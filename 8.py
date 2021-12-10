#!/usr/bin/env python3

import numpy as np
import copy
import itertools

state00="acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"

state0 = """be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce"""

d0 = ['a', 'b', 'c', 'e', 'f', 'g']
d1 = ['c', 'f']
d2 = ['a', 'c', 'd', 'e', 'g']
d3 = ['a', 'c', 'd', 'f', 'g']
d4 = ['b', 'c', 'd', 'f']
d5 = ['a', 'b', 'd', 'f', 'g']
d6 = ['a', 'b', 'd', 'e', 'f', 'g']
d7 = ['a', 'c', 'f']
d8 = ['a', 'b', 'c', 'd', 'e', 'f', 'g']
d9 = ['a', 'b', 'c', 'd', 'f', 'g']
d = [d0, d1, d2, d3, d4, d5, d6, d7, d8, d9]

segments = ['a', 'b', 'c', 'd', 'e', 'f', 'g']


def check(input):
    input2 = [sorted(x) for x in [y for y in input]]
    #print(input2)
    for p in itertools.permutations(segments):
        mapping = dict(zip(segments, p))
        pdigits = [sorted([mapping[y] for y in x]) for x in d]
        #print(pdigits)
        ok = True
        for i in input2:
            if i not in pdigits:
                ok = False
                break
        if ok:
            return p
    return None

def part0():
    lines = [line.split('|')[1].strip().split(' ') for line in state0.split('\n')]
    flat = [len(item) for sublist in lines for item in sublist]
    c = flat.count(2) + flat.count(3) + flat.count(4) + flat.count(7)
    assert 26 == c


def part00_2():
    line = state00
    l = (line.split('|')[0].strip().split(' '), line.split('|')[1].strip().split(' '))
    print(l)
    p = check(l[0])
    mapping = list(zip(segments, p))
    mapping_rev = dict([(y,x) for (x,y) in mapping])
    print(l[1])
    num = [d.index(sorted([mapping_rev[x] for x in digits])) for digits in l[1]]
    print(num)
    assert [5,3,5,3] == num

def part0_2():
    lines = [(line.split('|')[0].strip().split(' '), line.split('|')[1].strip().split(' ')) for line in state0.split('\n')]
    res = []
    for line in lines:
        p = check(line[0])
        mapping = list(zip(segments, p))
        mapping_rev = dict([(y,x) for (x,y) in mapping])
        num = int("".join([str(d.index(sorted([mapping_rev[x] for x in digits]))) for digits in line[1]]))
        res.append(num)
    print(res)
    assert 61229 == sum(res)

def part1():
    lines = [line.split('|')[1].strip().split(' ') for line in open('input8.txt')]
    flat = [len(item) for sublist in lines for item in sublist]
    c = flat.count(2) + flat.count(3) + flat.count(4) + flat.count(7)
    print(c)

def part2():
    lines = [(line.split('|')[0].strip().split(' '), line.split('|')[1].strip().split(' ')) for line in open('input8.txt')]
    res = []
    for line in lines:
        p = check(line[0])
        mapping = list(zip(segments, p))
        mapping_rev = dict([(y,x) for (x,y) in mapping])
        num = int("".join([str(d.index(sorted([mapping_rev[x] for x in digits]))) for digits in line[1]]))
        res.append(num)
    print(sum(res), res)
    assert 1012272 == sum(res)

if __name__ == '__main__':
    part0()
    part1()
    part00_2()
    part0_2()
    part2()
