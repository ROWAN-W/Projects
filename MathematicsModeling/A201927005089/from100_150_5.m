%计算5s内从100 到150 所需开启时间 即变量 t


for t=0.287:0.001:0.8
     rho0=0.850;
       for i=1:50
           m=min(abs(cor(2,:)-rho0));
           if m==0
               pre0=cor(1,cor(2,:)==rho0);
           else
            
               y=find(abs(cor(2,:)-rho0*ones(1,201))==min(abs(cor(2,:)-rho0)));
               pre0=cor(1,y);
           end
           dm=0.8711*0.85*0.49*pi*(2*(160-pre0)/0.8711)^0.5*t*(100/(t+10))-44*rho0;
           rho1=(rho0*39250+dm)/39250; 
           rho0=rho1;
           m=min(abs(cor(2,:)-rho1));
           if m==0
               press=cor(1,cor(2,:)==rho1);
           else
               x=find(abs(cor(2,:)-rho0*ones(1,201))==min(abs(cor(2,:)-rho0)));
         
               press=cor(1,x);
           end
       end
       if press>=150
        break
       end
end