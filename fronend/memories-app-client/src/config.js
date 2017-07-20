export default {
	s3: {
  BUCKET: 'memories.app.content'
},
apiGateway: {
  URL: 'https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod',
},
  cognito: {
  	MAX_ATTACHMENT_SIZE: 5000000,
    USER_POOL_ID : 'us-west-2_AIpt9A4xB',
    APP_CLIENT_ID : '5m67etac0v89nk2na84m52gujd',
    REGION: 'us-west-2',
	IDENTITY_POOL_ID: 'us-west-2:78b07b86-89fa-4889-aa59-7af9e9e15f89',
  }
};