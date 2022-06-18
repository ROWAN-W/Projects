%计算高压油泵进入高压油管的油量

function ret=in(x,D,F)
z=8.258;

Mo=5.532*pi*2.5*2.5*0.85;
M=Mo;
iM=39250*0.85;
q1=(5.532*0.85)/(z-D(1,2));
iq1=0.85;

b=min(abs(F(2,:)-q1));
if b==0
  p1=F(1,F(2,:)==q1);
else
  y=find(abs(F(2,:)-q1*ones(1,401))==min(abs(F(2,:)-q1)));
  p1=F(1,y);
end

b=min(abs(F(2,:)-iq1));
if b==0
  ip1=F(1,F(2,:)==iq1);
else
  y=find(abs(F(2,:)-iq1*ones(1,401))==min(abs(F(2,:)-iq1)));
  ip1=F(1,y);
end


d1=0.85*0.49*pi*(2*(p1-ip1)/q1)^0.5*q1*x;
allp=zeros(1,525);
allp(1)=p1;
for i=2:525
    M=M-d1;
    iM=iM+d1;
    q=M/(2.5^2*pi*(z-D(i,2)));
    iq=iM/39250;
    b=min(abs(F(2,:)-q));
 if b==0
  p=F(1,F(2,:)==q);
  else
  y=find(abs(F(2,:)-q*ones(1,401))==min(abs(F(2,:)-q)));
  p=F(1,y);
 end
 b=min(abs(F(2,:)-iq));
if b==0
  ip=F(1,F(2,:)==iq);
else
  y=find(abs(F(2,:)-iq*ones(1,401))==min(abs(F(2,:)-iq)));
  ip=F(1,y);
end
if p>=ip
 d1=0.85*0.49*pi*(2*(p-ip)/q)^0.5*q*x;  
     allp(i)=p;
else
    d1=0;
 end
    
end
 
ret=[Mo-M,max(allp),length(find(allp))];