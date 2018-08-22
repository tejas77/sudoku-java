package sudoku;

import java.util.Scanner;

public class Sudoku {
		int arr[][][] = new int[9][9][10];
		int chk[] = new int[10];
		int[] chkr = new int[10];
		int[] chkc = new int[10];
		int[] chkb = new int[10];
		int i,j,k,l,val,c,s=0,temp,loop=1;

	public void initialize(){
		for(i=0;i<9;i++)
		{
			for(j=0;j<9;j++)
			{
				arr[i][j][0] = 0;
				for(k=1;k<10;k++){
					arr[i][j][k] = k;
				}
			}
		}	
	}

	public boolean box(int ib,int jb){
		chkinit(chkb);
		int si = ib-ib%3;
		int sj = jb-jb%3;
		int n = 0;
		int sil = si+3;
		int sjl = sj+3;
		for(si=ib-ib%3;si<sil;si++){
			for(sj=jb-jb%3;sj<sjl;sj++){
				if(arr[si][sj][0] != 0 && arr[si][sj][0] != arr[ib][jb][0]){ 
					chkb[arr[si][sj][0]] = 0;
					n++;
				}
			}
		}
		for(si = ib-ib%3;si<sil;si++){
			for(sj = jb-jb%3;sj<sjl;sj++){
				if(arr[si][sj][0] == 0){
					for(l = 1;l<10;l++){
						if(chkb[l] == 0){
						arr[si][sj][l] = 0;}
					}
				}
			}
		}
		if(n == 8){
			for(l=1;l<10;l++){
				if(chkb[l] != 0){
					for(k = 1;k<10;k++){
						arr[ib][jb][k] = 0;
					}
					arr[ib][jb][0] = chkb[l];
					for(c=0;c<9;c++){
						if(arr[c][jb][0] == 0){
							arr[c][jb][chkb[l]] = 0;
						}
						if(arr[ib][c][0] == 0){
							arr[ib][c][chkb[l]] = 0;
						}
					}
				}
			}
			return true;
		}
		return false;
		
	}
	public void input(){
		Scanner sc = new Scanner(System.in);
		while(val != -1)
		{
			System.out.println("Enter Predefined Numbers in tiles as i,j, value : ");
			i = sc.nextInt();
			j = sc.nextInt();
			val = sc.nextInt();
			if(val != -1)
			{
				arr[i-1][j-1][0]=val;
				for(k=1;k<10;k++){
					arr[i-1][j-1][k] = 0;
				}
				
			}
			
		} 
		sc.close();
	}
	public void chkinit(int[] ref){
		for(c=1;c<10;c++)
		{
			ref[c] = c;
		}	
	}
	public void display(){
		System.out.println("=============================");
		for(i=0;i<9;i++)
		{
			for(j=0;j<9;j++)
			{
				if(j%3 == 0)
				{
					System.out.print(" | ");
				}			
				System.out.print(arr[i][j][0]); 
				
			}
			System.out.println(" | ");
		}
		System.out.println("=============================");
	}
	public void algo(){

	while(loop == 1)
	{	

		for(i=0;i<9;i++)
		{
			for(j=0;j<9;j++)
			{
				if(arr[i][j][0] == 0)
				{
					chkinit(chk);
					chkinit(chkc);
					chkinit(chkr);
					if(!box(i,j)){
					for(c=0;c<9;c++)
					{
						if( arr[i][c][0] ==1 || arr[i][c][0] ==2 || arr[i][c][0] ==3 ||
						    arr[i][c][0] ==4 || arr[i][c][0] ==5 || arr[i][c][0] ==6 || 
						    arr[i][c][0] ==7 || arr[i][c][0] ==8 || arr[i][c][0] ==9 )
						{
						chk[arr[i][c][0]]=0;
						chkr[arr[i][c][0]]=0;
						}
						
						
					}
					for(c=0;c<9;c++){
						if(arr[i][c][0] == 0){
							for(l = 1;l<10;l++){
								if(chkr[l] == 0){
								arr[i][c][l] = 0;}
							}			
						}
					}
					for(c=0;c<9;c++)
					{
						if( arr[c][j][0] ==1 || arr[c][j][0] ==2 || arr[c][j][0] ==3 ||
							arr[c][j][0] ==4 || arr[c][j][0] ==5 || arr[c][j][0] ==6 || 
							arr[c][j][0] ==7 || arr[c][j][0] ==8 || arr[c][j][0] ==9 )
						{				
						chk[arr[c][j][0]]=0;
						chkc[arr[c][j][0]]=0;
						} 
					}
					for(c=0;c<9;c++){
						if(arr[c][j][0] == 0){
							for(l = 1;l<10;l++){
								if(chkc[l] == 0){
								arr[c][j][l] = 0;}
							}	
						}
					}
					for(c=1;c<10;c++)
					{
						if(chk[c] !=0)
						{
							temp = chk[c];
							s+=1;
						}	
					}
					if(s == 1)
					{
						s = 0;
						
						for(l = 1;l<10;l++){
							arr[i][j][l] = 0;
						}	
						arr[i][j][0] = temp;
						for(c=0;c<9;c++){
							if(arr[c][j][0] == 0){
								arr[c][j][temp] = 0;
							}
							if(arr[i][c][0] == 0){
								arr[i][c][temp] = 0;
							}
						}
						
					}
					else
					{	s = 0; 
					}	
				}
			    }
			}
		}
		s = 0;
		loop = 0;
		for(i=0;i<9;i++)
		{
			for(j=0;j<9;j++)
			{
				if(arr[i][j][0] == 0){
					chkinit(chk);
					for(l = 1;l<10;l++){
						chk[l] = arr[i][j][l];
					}
					for(c=1;c<10;c++)
					{
						if(chk[c] !=0)
						{
							temp = chk[c];
							s+=1;
						}	
					}
					if(s == 1)
					{
						s = 0;
						for(l = 1;l<10;l++){
							arr[i][j][l] = 0;
						}
						arr[i][j][0] = temp;
						for(c=0;c<9;c++){
							if(arr[c][j][0] == 0){
								arr[c][j][temp] = 0;
							}
							if(arr[i][c][0] == 0){
								arr[i][c][temp] = 0;
							}
						}
					}
					else
					{	s = 0; 
					}
				}
			}
		}
		
		for(i=0;i<9;i++)
		{
			for(j=0;j<9;j++)
			{
				if(arr[i][j][0] == 0)
				{ loop = 1;
				break;
				}				
			}
		}
		display();
	 }
	}
}
