#include <bits/stdc++.h>
using namespace std;
typedef long long ll;
typedef vector<ll> vll;
typedef vector<vll> vvll;
typedef pair<ll,ll> pll;
typedef vector<pll> vpll;
#define PB push_back
#define P1 first
#define P2 second
const ll MN=7501;
ll fac[MN];
vll choose[MN];
//mcombo[m][k]=# sets of k unordered disjoint combo.s of m elem.s each, selected from m*k elem.s
// =PROD_{r=1 to k}(nCr(rm-1,m-1))
bool isprime[MN];
ll M;
ll mod(ll n, ll k) {
	return (n%k+k)%k;
}
typedef unsigned long long ull;
typedef __uint128_t L;
struct FastMod {
	ull b, m;
	FastMod(ull b) : b(b), m(ull((L(1) << 64) / b)) {}
	ull mod(ull a) {
    if (a<0) return (a%m+m)%m;
    ull q = (ull)((L(m) * a) >> 64);
    ull r = a - q * b; // can be proven that 0 <= r < 2*b
    return r >= b ? r - b : r;
	}
	ll expmod(ll n, ll k) {
    if (k==0) return 1;
    if (k==1) return mod(n);
    ll h=expmod(n,k/2);
    h=mod(h*h);
    return k%2==0?h:mod(h*n);
	}
};
FastMod MA(2), MB(2);
ll permcnt(ll N, ll d) {
	//dp[n]=# n-len permutations w/ no cycle lengths divisible by d, mod M-1
	//dp[n]=SUM_{m=1 to n, m not multiple of d}(dp[n-m]*nCr(n-1,m-1)*(m-1)!)
	// =g[n]-h[n],
	// where g[n]=SUM_{m=1 to n}(dp[n-m]*nCr(n-1,m-1)*(m-1)!)
	// --> g[n]=dp[n-1]+(n-1)*g[n-1]
	// and h[n]=SUM_{k=1 to n/d}(dp[n-kd]*nCr(n-1,kd-1)*(kd-1)!)
	// --> h[n]=dp[n-d]*nCr(n-1,d-1)*(d-1)!+nCr(n-1,d)*d!*h[n-D]
	ll dp[N+1], g[N+1], h[N+1];
	dp[0]=1;
	g[0]=h[0]=0;
	for (ll n=1; n<=N; n++) {
	g[n]=MB.mod(dp[n-1]+MB.mod((n-1)*g[n-1]));
	h[n]=n<d?0:n==d?fac[d-1]:
    MB.mod(
      MB.mod(MB.mod(dp[n-d]*choose[n-1][d-1])*fac[d-1])
      +MB.mod(MB.mod(choose[n-1][d]*fac[d])*h[n-d])
    );
	dp[n]=mod(g[n]-h[n],M-1);
	}
	return mod(fac[N]-dp[N],M-1);
}
int main() {
	ll N; cin>>N>>M;
	MA=FastMod(M);
	MB=FastMod(M-1);
	fac[0]=1;
	for (ll i=1; i<=N; i++)
	  fac[i]=MB.mod(i*fac[i-1]);
	for (ll n=0; n<=N; n++) {
    choose[n]=vll(n+1);
    for (ll k=0; k<=n; k++)
    choose[n][k]=(k==0||k==N)?1:
    MB.mod((n>k?choose[n-1][k]:0)+choose[n-1][k-1]);
	}
	for (ll i=2; i<=N; i++)
	  isprime[i]=true;
	for (ll i=2; i<=N; i++)
    if (isprime[i])
      for (ll m=2*i; m<=N; m+=i)
        isprime[m]=false;
	ll ans=1;
	for (ll p=2; p<=N; p++)
    if (isprime[p])
      for (ll d=p; d<=N; d*=p)
        ans=MA.mod(ans*MA.expmod(p,permcnt(N,d)));
	cout<<ans<<"\n";
}
