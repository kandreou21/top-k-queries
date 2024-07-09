# Top-k Queries

This repository contains an implementation of a Top-k Queries algorithm in Java, which aggregates individual item scores from three sources. The items can be restaurants, with each source representing a restaurant review site (e.g., Google, TripAdvisor, Yelp). The individual score of an item in one source is the average of the ratings it received on that site.

## Overview

The Top-k Queries algorithm is designed to aggregate scores from multiple sources and identify the top `k` items based on their combined scores.

## Data Sources

The data is located in three files: `seq1.txt`, `seq2.txt`, and `rnd.txt`.

1. **Sequential Sources (seq1.txt and seq2.txt)**
   - Access: Sequential (serial) access only
   - Sorting: Items are sorted in descending order based on their individual scores in each source.

2. **Random Access Source (rnd.txt)**
   - Access: Random access
   - Sorting: Items are sorted based on their IDs.

## Requirements

- Java Development Kit (JDK) 12 or higher
