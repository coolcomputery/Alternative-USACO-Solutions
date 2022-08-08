import java.io.*;
import java.util.*;
public class Dec2020P1 {
    private static final long MOD=1000_000_007;
    private static long mod(long n) {
        return ((n%MOD)+MOD)%MOD;
    }
    public static void main(String[] args) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        int N=Integer.parseInt(in.readLine());
        int[] R=new int[N], Q=new int[N];
        long[] fac=new long[N]; {
            int[] S=new int[N], T=new int[N];
            StringTokenizer tok=new StringTokenizer(in.readLine());
            for (int i=0; i<N; i++) S[i]=Integer.parseInt(tok.nextToken());
            Arrays.sort(S);
            tok=new StringTokenizer(in.readLine());
            for (int i=0; i<N; i++) T[i]=Integer.parseInt(tok.nextToken());
            Arrays.sort(T);
            int r=0;
            for (int i=0; i<N; i++) {
                while (r<N && S[i]>T[r]) r++;
                R[i]=r;
            }
            int q=0;
            for (int i=0; i<N; i++) {
                while (q<N && S[q]<=T[i]) q++;
                Q[i]=q;
            }
//            System.err.println(Arrays.toString(R)+" "+Arrays.toString(Q));
            fac[0]=1;
            for (int i=1; i<N; i++) fac[i]=mod(i*fac[i-1]);
            //cow i can fit in barns R[i]...N-1; barn i can fit cows 0...Q[i]-1
        }


            /*
            fix x = min idx of unmatched cow
            --> all barns R[x]...N-1 and all cows 0...x-1 must be matched
            x=N --> no cow is unmatched, define R[N]=N
            because x is specifically the min idx of any unmatched cow,
              these conditions on which barns and cows are forced to be matched
                  are sufficient and necessary for us to have a maximal matching

            when doing dp,
                we want to fix the largest unprocessed cow,
                since that way, at any cow i,
                all barns that have been matched in a previous DP state (in top-down order)
                    must be in the interval [R[i],N),
                i.e. the interval of all barns that cow i can fit in,
            we need to keep track of how many barns have currently been matched in intervals [R[x],N) and [0,R[x])
                since cows i<x can fit in barns j>=R[x]
            dp(n,b,p)=# maximal matchings in which we are processing cow i=n-1,
                and b,p barns in intervals [R[x],N),[0,R[x]) resp. have been matched so far
            if i>x: match cow --> ((N-R[i])-b)*dp(n-1,b+1,p) ; do not match --> dp(n-1,b,p)
            if i==x: do not match --> dp(n-1,b,p)
            if i<x: match to [R[x],N) --> ((N-R[x])-b)*dp(n-1,b+1,p) ; match to [0,R[x]) --> ((R[x]-R[i])-p)*dp(n-1,b,p+1)
            base case: dp(0,b,p)=1 if b==N-R[x] else 0 (must ensure all barns in [R[x],N) have been matched)
            desired answer: dp(N,0,0)
            time: O(N^3) for each x --> O(N^4) total

            optimizations:
            for i>=x (n>x), we always have p=0 --> O(N^2) dp states
            for i<x (n<=x), we have O(N^3) dp states to process

            idea: try DPing on barns <R[x] after DPing on cows >=x; hopefully we will only need O(N^2) states
            then, if there are k cows <x and m barns >=R[x] left unmatched:
                if k==m, then we can match the cows and barns in k! ways, since every cow <x can match with every barn >=R[x];
                else, we cannot get a maximal matching, since we cannot get all cows <x and all barns >=R[x] to be matched

            let y=R[x] and Q[y]:=min i s.t. cow i CANNOT fit in barn y --> barn y can be matched with cows <Q[y]
            we want to fix how many cows >x and barns <y get matched, since this will fix how many barns >=y and cows <x get matched
            --> we need DP over cows in the opposite order we were doing before
            f(n,b)=# matchings for cows [n,N) where b cows WILL BE matched   = f(n+1,b) + b==0?0:((N-R[n])-(b-1))*f(n+1,b-1)
            g(n,c)=# matchings for barns [0,n) where c barns will be matched = g(n-1,b) + c==0?0:(Q[n-1]-(c-1))*g(n-1,c-1)
            f(N,b)=1 if b==0 else 0, g(0,c)=1 if c==0 else 0; f(n,b)=0 if b<0, g(n,c)=0 if c<0
            --> after processing barns <y, cows >x: # matchings with b barns >=y matched and c cows <x matched == f(N,b)*g(0,c)
            --> if x-c==(N-y)-b==k: # maximal matchings == k!*f(x+1,b)*g(y,c)
                since every cow <x can fit in every barn >=y
            time: O(N^2) for each x --> O(N^3) total

            more optimizations (very easy this time):
            f,g do not change with x
            when iterating over each b,c s.t. x-c==(N-y)-b,
                we can simply iterate over c and set b=(N-y)-(x-c)
                (and check necessary boundary conditions)
            time: O(N^2) total
             */
        long out=0;
        long[][] f=new long[N+1][N+1], g=new long[N+1][N+1];
        f[N][0]=1;
        for (int n=N-1; n>0; n--) for (int b=0; b<=N; b++)
            f[n][b]=mod(
                    f[n+1][b]
                    +(b==0?0:mod(((N-R[n])-(b-1))*f[n+1][b-1]))
            );
        g[0][0]=1;
        for (int n=1; n<=N; n++) for (int c=0; c<=N; c++)
            g[n][c]=mod(
                    g[n-1][c]
                    +(c==0?0:mod((Q[n-1]-(c-1))*g[n-1][c-1]))
            );
        for (int x=0; x<=N; x++) {
            int y=x<N?R[x]:N;
            for (int c=0; c<=x; c++) {
                int k=x-c;
                if (k<=N-y) out=mod(out+mod(fac[k]*mod((x==N?1:f[x+1][(N-y)-k])*g[y][c])));
            }
        }
        System.out.println(out);
    }
}
