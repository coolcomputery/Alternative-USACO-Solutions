# Alternative-USACO-Solutions

## December 2020 Platinum #1
We sort the cows and barns by nondecreasing size, but this time we keep cows and barns in separate lists. Let `R[i]` be the least `j` s.t. cow `i` can fit in barn `j` (set `R[i]=N` if no such `j` exists), and `Q[j]` be the least `i` s.t. cow `i` cannot fit in barn `j` (set `Q[j]=N` if no such `i` exists). Then cow `i` can fit in barns `[R[i], N)` and barn `j` can fit cows `[0,Q[j])` (note the half-open intervals).

Fix `x` as the index of the leftmost cow that will be forcefully unmatched, and `y=R[x]` as the index of the leftmost barn that cow `x` can fit in; for the case where there are no unmatched cows, we set `x=N` and `y=N`.

The high-level idea of the algorithm is to do a DP over cows `[x+1,N)`, then do another DP over barns to `[0,y)`, while fixing the number of total matchings we do in each DP. We notice that cows `[x+1,N)` can only match with barns `[y,N)`, and barns `[0,y)` can only match with cows `[0,x)` (note that the converse is not necessarily true). This means that the matchings we make in the DPs will not make the DPs interfere with one another.

Then, for each (incomplete) matching we get after doing these two DPs, if we have exactly `k` unmatched cows `[0,x)` and exactly `k` unmatched barns `[y,N)` for some number `k`, we generate `k!` maximal matchings, since our condition on `x` forces all cows `[0,x)` and all barns `[y,N)` to be matched, and every cow `[0,x)` can fit in every barn `[y,N)`; if the numbers of unmatched cows and unmatched barns in these intervals are not equal, we cannot generate any maximal matchings.

Now for the calculations: let

`f(n,b)`=# (incomplete) matchings of cows [n,N) with all barns, where b matches are made;

`g(n,c)`=# (incomplete) matchings of barns [0,n) with all cows, where c matches are made;

For `f`: if we do not match cow `n`, we get `f(n+1,b)` matchings; if we match, then (assuming `b>0`) there were `b-1` matches before we match cow `n`, so there are `(N-R[n])-(b-1)` barns that cow `n` could match to. Thus, we have:

`f(n,b)=f(n+1,b)+( ((N-R[n])-(b-1))*f(n+1,b-1) if b>0 else 0 )`

Using similar logic for g, we have:

`g(n,c)=g(n-1,c)+( (Q[n-1]-(c-1))*g(n-1,c-1) if c>0 else 0 )`

Base cases are:
if `n>=N`: `f(n,b)=(1 if b==0 else 0)`;
if `n<=0`: `g(0,c)=(1 if c==0 else 0)`;
This is because in each of these cases, there are either no cows or no barns to match.
We also obviously have `f(n,b)=0 if b<0` and `g(n,c)=0 if c<0`.

To count the number of maximal matchings where `x` is the leftmost unmatched cow, let k be the number of cows `[0,x)` not matched by barns `[0,y)`, as well as the number of barns `[y,N)` not matched by `[x+1,N)`. This means that `x-k` cows `[0,x)` are matched by barns `[0,y)`, and `(N-y)-k` barns `[y,N)` are matched by cows `[x+1,N)`, so we have `f(x+1,(N-y)-k)*g(y,x-k)` incomplete matchings. Thus, we increase the number of maximal matchings we have calculated so far by `k!*f(x+1,(N-y)-k)*g(y,x-k)`, for `0<=k<=N`. (In my code I did this last part in a slightly different but equivalent way). We set `f(N+1,b)` to be equal to `f(N,b)`.

Since `f` and `g` are independent of `x`, we can precompute them before iterating over `x`. Afterwards, we do `O(N)` work for each `x`. Total runtime is `O(N^2)`.

## US Open 2020 Platinum #2
Following the official solution, the product of periods of all permutations over N elements is equal to:

```PROD_{prime p, nat num k} p^f(N,D), where f(N,D)=(# of perm.s over N elem.s w/ at least one cycle length divisible by D=p^k)```

For each D, we can evaluate `f(N,D)` as `N!-dp(N)`, where `dp(n)`=(# of perm.s over n elem.s w/ no cycle length divisible by D). To solve this dp, we can think about the last element: if it is in a cycle of length m, then there are `nCr(n-1,m-1)*(m-1)!` possible cycles containing this last element, and the remaining `n-m` elem.s can be put into any permutation w/ no cycle length divisible by D, so:

```dp(n)=SUM_{m=1 to n, m not a multiple of D}(dp(n-m)*nCr(n-1,m-1)*(m-1)!)```

That "m not a multiple of D" requirement makes the summation annoying, so we can move it out:

```dp(n)=SUM_{m=1 to n}(dp(n-m)*nCr(n-1,m-1)*(m-1)!)-SUM_{k=1 to floor(n/d)}(dp(n-kd)*nCr(n-1,kd-1)*(kd-1)!)```

Write the first part of this formula as `g(n)`. If we expand `g(n)`, we notice that its formula is very similar to that of `g(n-1)`. Through algebraic observation, we can find that `g(n)=dp(n-1)+(n-1)*g(n-1)`. Our DP now runs in `O(N^2/D)` time, which is the same as USACO's partial solution for the subtask `N<=3000`).

Now this is where my solution and USACO's solution differ. We can write the second part of the `dp(n)` formula `h(n)`, and notice that the expression for `h(n)` is very similar to that of `h(n-d)`. Through more algebra, we can determine that ```h(n)=dp(n-d)*nCr(n-1,d-1)*(d-1)!+nCr(n-1,d)*d!*h(n-d)```. Thus, our DP now runs in `O(N)` time, which is better than the `O(N^2/D^2)`-time DP that USACO ends with.

Since there are `O(N)` possible values for `D`, i.e. prime powers from 1 to `N`, our total runtime is `O(N^2)`.
