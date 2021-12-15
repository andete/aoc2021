#!/usr/bin/env python3

import numpy as np
import copy
import itertools

example = """\
NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C"""

def apply_rule(pairs, rule, singles):
    pairs_res = {}
    existing = pairs[rule[0]]
    if existing > 0:
        pairs[rule[0]] = 0
        for n in rule[1]:
            pairs_res[n] = existing
        singles[rule[2]] += existing
    return pairs_res


def round(pairs, rules, singles):
    resl = []
    for rule in rules:
        resl.append(apply_rule(pairs, rule, singles))
    for res in resl:
        for r in res:
            pairs[r] += res[r]

def count_pairs(formula, rules):
    res = {}
    for rule in rules:
        res[rule[0]] = 0
    for i in range(len(formula) -1):
        for rule in rules:
            if formula[i:i+2] == rule[0]:
                res[rule[0]] += 1
    return res

def count_singles(chars, formula):
    res = {}
    for c in chars:
        res[c] = 0
    for c in formula:
        res[c] += 1
    return res

def load(lines):
    chars = set([c for c in "".join(lines) if ord(c) >= ord('A') and ord(c) <= ord('Z')])
    formula = lines[0]
    rules = [(y[0], [y[0][0]+y[1], y[1]+y[0][1]], y[1]) for y in [x.split(' -> ') for x in lines[2:]]]
    pairs = count_pairs(formula, rules)
    singles = count_singles(chars, formula)
    return (formula, chars, rules, singles, pairs)

def result(singles):
    ma = max(singles.values())
    mi = min(singles.values())
    return ma - mi

def part0():
    (formula, chars, rules, singles, pairs) = load(example.split('\n'))

    round(pairs, rules, singles)
    assert count_pairs("NCNBCHB", rules) == pairs
    assert count_singles(chars, "NCNBCHB") == singles

    round(pairs, rules, singles)
    assert count_pairs("NBCCNBBBCBHCB", rules) == pairs
    assert count_singles(chars, "NBCCNBBBCBHCB") == singles

    round(pairs, rules, singles)
    assert count_pairs("NBBBCNCCNBBNBNBBCHBHHBCHB", rules) == pairs
    assert count_singles(chars, "NBBBCNCCNBBNBNBBCHBHHBCHB") == singles

    round(pairs, rules, singles)
    assert count_pairs("NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB", rules) == pairs
    assert count_singles(chars,"NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB") == singles
    for _ in range(10-4):
        round(pairs, rules, singles)

    assert 1588 == result(singles)

def part1():
    (formula, chars, rules, singles, pairs) = load([line.strip() for line in open('input14.txt')])
    for i in range(10):
        round(pairs, rules, singles)
    res = result(singles)
    print(res)
    assert 2170 == res


def part2():
    (formula, chars, rules, singles, pairs) = load([line.strip() for line in open('input14.txt')])
    for i in range(40):
        round(pairs, rules, singles)
    res = result(singles)
    print(res)
    assert 2422444761283 == res

if __name__ == '__main__':
    part0()
    part1()
    part2()
